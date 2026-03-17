package com.brian.portfolioapi.controller;

import com.brian.portfolioapi.dto.AdminProjectDetailResponse;
import com.brian.portfolioapi.dto.AdminProjectSummaryResponse;
import com.brian.portfolioapi.dto.PageResponse;
import com.brian.portfolioapi.dto.ProjectDetailResponse;
import com.brian.portfolioapi.dto.ProjectUpsertRequest;
import com.brian.portfolioapi.mapper.ProjectMapper;
import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.service.AdminProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Admin Projects", description = "Admin-only endpoints to manage portfolio projects")
@SecurityRequirement(name = "adminToken")
@Validated
@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "updatedAt",
            "createdAt",
            "publishedAt",
            "title",
            "slug",
            "status"
    );

    private final AdminProjectService service;

    public AdminProjectController(AdminProjectService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<AdminProjectSummaryResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.listAll(pageable);
    }

    @GetMapping("/{slug}")
    public AdminProjectDetailResponse detail(@PathVariable String slug) {
        return service.getDetailBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDetailResponse create(
            @Valid @RequestBody ProjectUpsertRequest req
    ) {
        Project created = service.create(req);
        return ProjectMapper.toDetail(created);
    }

    @PutMapping("/{slug}")
    public ProjectDetailResponse update(
            @PathVariable String slug,
            @Valid @RequestBody ProjectUpsertRequest req
    ) {
        Project updated = service.updateBySlug(slug, req);
        return ProjectMapper.toDetail(updated);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String slug) {
        service.deleteBySlug(slug);
    }

    private Sort parseSort(String sort) {
        String normalizedSort = (sort == null || sort.isBlank())
                ? "updatedAt,desc"
                : sort.trim();

        String[] parts = normalizedSort.split(",", 2);
        String field = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException(
                    "Unsupported sort field: " + field + ". Allowed values: updatedAt, createdAt, publishedAt, title, slug, status"
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
