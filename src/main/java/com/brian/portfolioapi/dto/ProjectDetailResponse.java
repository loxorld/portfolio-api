package com.brian.portfolioapi.dto;

import com.brian.portfolioapi.model.ProjectStage;

import java.time.Instant;
import java.util.List;

public record ProjectDetailResponse(
        String slug,
        String title,
        String summary,
        String description,
        ProjectStage stage,
        Instant publishedAt,
        List<String> tags,
        List<String> stack,
        List<String> imageUrls,
        String demoUrl,
        String repoUrl
) {}
