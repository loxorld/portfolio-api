package com.brian.portfolioapi.controller;

import com.brian.portfolioapi.config.AdminProperties;
import com.brian.portfolioapi.dto.ProjectDetailResponse;
import com.brian.portfolioapi.dto.ProjectUpsertRequest;
import com.brian.portfolioapi.exception.ForbiddenException;
import com.brian.portfolioapi.exception.UnauthorizedException;
import com.brian.portfolioapi.mapper.ProjectMapper;
import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.service.AdminProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Projects", description = "Admin-only endpoints to manage portfolio projects")
@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final AdminProjectService service;
    private final AdminProperties admin;

    public AdminProjectController(AdminProjectService service, AdminProperties admin) {
        this.service = service;
        this.admin = admin;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDetailResponse create(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @Valid @RequestBody ProjectUpsertRequest req
    ) {
        authorize(token);
        Project created = service.create(req);
        return ProjectMapper.toDetail(created);
    }

    @PutMapping("/{slug}")
    public ProjectDetailResponse update(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @PathVariable String slug,
            @Valid @RequestBody ProjectUpsertRequest req
    ) {
        authorize(token);
        Project updated = service.updateBySlug(slug, req);
        return ProjectMapper.toDetail(updated);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @PathVariable String slug
    ) {
        authorize(token);
        service.deleteBySlug(slug);
    }

    private void authorize(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Missing X-Admin-Token header");
        }
        if (admin.token() == null || admin.token().isBlank()) {
            throw new ForbiddenException("Admin token is not configured on server");
        }
        if (!admin.token().equals(token)) {
            throw new ForbiddenException("Invalid admin token");
        }
    }
}
