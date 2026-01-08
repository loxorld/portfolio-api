package com.brian.portfolioapi.service;

import com.brian.portfolioapi.dto.ProjectUpsertRequest;
import com.brian.portfolioapi.exception.NotFoundException;
import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
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

    @Transactional
    public Project create(ProjectUpsertRequest req) {
        // Slug único para evitar choque
        if (repo.existsBySlug(req.slug())) {
            throw new IllegalArgumentException("Slug already exists: " + req.slug());
        }

        Project p = new Project();
        apply(p, req);
        return repo.save(p);
    }

    @Transactional
    public Project updateBySlug(String slug, ProjectUpsertRequest req) {
        Project p = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project not found: " + slug));

        // Permitimos cambiar slug, pero controlamos colisión
        if (!slug.equals(req.slug()) && repo.existsBySlug(req.slug())) {
            throw new IllegalArgumentException("Slug already exists: " + req.slug());
        }

        apply(p, req);
        return repo.save(p);
    }

    @Transactional
    public void deleteBySlug(String slug) {
        Project p = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Project not found: " + slug));
        repo.delete(p);
    }

    private void apply(Project p, ProjectUpsertRequest req) {
        p.setSlug(req.slug().trim());
        p.setTitle(req.title().trim());
        p.setSummary(req.summary().trim());
        p.setDescription(req.description());

        p.setTags(req.tags() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(req.tags()));
        p.setStack(new LinkedHashSet<>(req.stack()));
        p.setImageUrls(req.imageUrls() == null ? List.of() : req.imageUrls());

        p.setRepoUrl(req.repoUrl());
        p.setDemoUrl(req.demoUrl());

        Instant publishedAt = req.publishedAt();
        if (publishedAt != null) {
            p.setStatus(ProjectStatus.PUBLISHED);
            p.setPublishedAt(publishedAt);
        } else {
            p.setStatus(ProjectStatus.DRAFT);
            p.setPublishedAt(null);
        }
    }
}
