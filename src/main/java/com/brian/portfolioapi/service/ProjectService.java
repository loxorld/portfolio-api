package com.brian.portfolioapi.service;

import com.brian.portfolioapi.dto.*;
import com.brian.portfolioapi.exception.NotFoundException;
import com.brian.portfolioapi.mapper.ProjectMapper;
import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectSummaryResponse> listPublished(String tag, Pageable pageable) {
        Page<Project> page = (tag == null || tag.isBlank())
                ? repo.findByStatus(ProjectStatus.PUBLISHED, pageable)
                : repo.findByStatusAndTag(ProjectStatus.PUBLISHED, tag.trim(), pageable);

        List<ProjectSummaryResponse> items = page.getContent()
                .stream()
                .map(ProjectMapper::toSummary)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getPublishedBySlug(String slug) {
        Project p = repo.findBySlugAndStatus(slug, ProjectStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        return ProjectMapper.toDetail(p);
    }
}
