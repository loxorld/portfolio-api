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


@Tag(name = "Projects", description = "Public portfolio projects (read-only)")
@Validated
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

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
        String[] parts = sort.split(",", 2);
        String field = parts[0];
        Sort.Direction dir =
                parts.length > 1 && parts[1].equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(dir, field);
    }
}