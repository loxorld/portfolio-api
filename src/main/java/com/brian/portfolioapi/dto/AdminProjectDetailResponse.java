package com.brian.portfolioapi.dto;

import com.brian.portfolioapi.model.ProjectStatus;

import java.time.Instant;
import java.util.List;

public record AdminProjectDetailResponse(
        String slug,
        String title,
        String summary,
        String description,
        ProjectStatus status,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt,
        List<String> tags,
        List<String> stack,
        List<String> imageUrls,
        String demoUrl,
        String repoUrl
) {}
