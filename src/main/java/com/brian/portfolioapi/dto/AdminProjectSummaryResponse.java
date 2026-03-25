package com.brian.portfolioapi.dto;

import com.brian.portfolioapi.model.ProjectStage;
import com.brian.portfolioapi.model.ProjectStatus;

import java.time.Instant;

public record AdminProjectSummaryResponse(
        String slug,
        String title,
        String summary,
        ProjectStatus status,
        ProjectStage stage,
        Instant publishedAt,
        Instant updatedAt
) {}
