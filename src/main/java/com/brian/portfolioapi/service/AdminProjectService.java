package com.brian.portfolioapi.service;

import com.brian.portfolioapi.dto.AdminProjectDetailResponse;
import com.brian.portfolioapi.dto.AdminProjectSummaryResponse;
import com.brian.portfolioapi.dto.PageResponse;
import com.brian.portfolioapi.dto.ProjectUpsertRequest;
import com.brian.portfolioapi.exception.NotFoundException;
import com.brian.portfolioapi.mapper.ProjectMapper;
import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStage;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class AdminProjectService {

    private final ProjectRepository repo;

    public AdminProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminProjectSummaryResponse> listAll(Pageable pageable) {
        Page<Project> page = repo.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(ProjectMapper::toAdminSummary).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Transactional(readOnly = true)
    public AdminProjectDetailResponse getDetailBySlug(String slug) {
        Project project = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project not found: " + slug));

        return ProjectMapper.toAdminDetail(project);
    }

    @Transactional
    public Project create(ProjectUpsertRequest req) {
        String normalizedSlug = normalizeSlug(req.slug());
        if (repo.existsBySlug(normalizedSlug)) {
            throw new IllegalArgumentException("Slug already exists: " + normalizedSlug);
        }

        Project p = new Project();
        apply(p, req, normalizedSlug);
        return repo.save(p);
    }

    @Transactional
    public Project updateBySlug(String slug, ProjectUpsertRequest req) {
        Project p = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project not found: " + slug));

        String normalizedSlug = normalizeSlug(slug);
        String normalizedReqSlug = normalizeSlug(req.slug());

        if (!normalizedSlug.equals(normalizedReqSlug) && repo.existsBySlug(normalizedReqSlug)) {
            throw new IllegalArgumentException("Slug already exists: " + normalizedReqSlug);
        }

        apply(p, req, normalizedReqSlug);
        return repo.save(p);
    }

    @Transactional
    public void deleteBySlug(String slug) {
        Project p = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project not found: " + slug));
        repo.delete(p);
    }

    private void apply(Project p, ProjectUpsertRequest req, String normalizedSlug) {
        p.setSlug(normalizedSlug);
        p.setTitle(req.title().trim());
        p.setSummary(req.summary().trim());
        p.setDescription(req.description());

        p.setTags(req.tags() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(req.tags()));
        p.setStack(new LinkedHashSet<>(req.stack()));
        p.setImageUrls(req.imageUrls() == null ? List.of() : req.imageUrls());

        p.setRepoUrl(req.repoUrl());
        p.setDemoUrl(req.demoUrl());
        p.setStage(req.stage() == null ? ProjectStage.STABLE : req.stage());

        Instant publishedAt = req.publishedAt();
        if (publishedAt != null) {
            p.setStatus(ProjectStatus.PUBLISHED);
            p.setPublishedAt(publishedAt);
        } else {
            p.setStatus(ProjectStatus.DRAFT);
            p.setPublishedAt(null);
        }
    }

    private String normalizeSlug(String slug) {
        return slug == null ? null : slug.toLowerCase();
    }
}
