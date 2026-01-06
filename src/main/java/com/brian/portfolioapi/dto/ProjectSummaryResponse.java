package com.brian.portfolioapi.dto;

import java.time.Instant;
import java.util.List;

public record ProjectSummaryResponse(
        String slug,
        String title,
        String summary,
        Instant publishedAt,
        List<String> tags,
        List<String> stack,
        String demoUrl,
        String repoUrl,
        String coverImageUrl
) {}
