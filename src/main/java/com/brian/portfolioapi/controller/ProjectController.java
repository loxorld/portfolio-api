package com.brian.portfolioapi.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.brian.portfolioapi.dto.PageResponse;
import com.brian.portfolioapi.dto.ProjectDetailResponse;
import com.brian.portfolioapi.dto.ProjectSummaryResponse;
import com.brian.portfolioapi.service.ProjectService;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Set;


@Tag(name = "Projects", description = "Public portfolio projects (read-only)")
@Validated
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("publishedAt", "title", "slug");

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @Operation(summary = "List published projects", description = "Returns published projects with optional tag filter and pagination.")
    @GetMapping
    public PageResponse<ProjectSummaryResponse> list(
            @Parameter(description = "Filter by tag (case-insensitive)")
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size,
            @Parameter(description = "Sort using one of: publishedAt,desc | publishedAt,asc | title,asc | title,desc | slug,asc | slug,desc")
            @RequestParam(defaultValue = "publishedAt,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.listPublished(tag, pageable);
    }

    @Operation(summary = "Get project detail by slug", description = "Returns a published project by its slug.")
    @GetMapping("/{slug}")
    public ProjectDetailResponse detail(@PathVariable @NotBlank String slug) {
        return service.getPublishedBySlug(slug);
    }

    private Sort parseSort(String sort) {
        String normalizedSort = (sort == null || sort.isBlank())
                ? "publishedAt,desc"
                : sort.trim();

        String[] parts = normalizedSort.split(",", 2);
        String field = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException(
                    "Unsupported sort field: " + field + ". Allowed values: publishedAt, title, slug"
            );
        }

        String rawDirection = parts.length > 1 ? parts[1].trim() : "desc";
        Sort.Direction dir = switch (rawDirection.toLowerCase()) {
            case "asc" -> Sort.Direction.ASC;
            case "desc", "" -> Sort.Direction.DESC;
            default -> throw new IllegalArgumentException(
                    "Unsupported sort direction: " + rawDirection + ". Allowed values: asc, desc"
            );
        };

        return Sort.by(dir, field);
    }
}
