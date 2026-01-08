package com.brian.portfolioapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ProjectUpsertRequest(
        @NotBlank String slug,
        @NotBlank String title,
        @NotBlank String summary,
        @NotBlank String description,

        Set<String> tags,
        @NotEmpty Set<String> stack,
        List<String> imageUrls,

        String repoUrl,
        String demoUrl,

        Instant publishedAt
) {}
