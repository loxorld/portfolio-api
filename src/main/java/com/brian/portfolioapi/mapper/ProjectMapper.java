package com.brian.portfolioapi.mapper;

import com.brian.portfolioapi.dto.ProjectDetailResponse;
import com.brian.portfolioapi.dto.ProjectSummaryResponse;
import com.brian.portfolioapi.model.Project;

import java.util.List;

public final class ProjectMapper {

    private ProjectMapper() {}

    public static ProjectSummaryResponse toSummary(Project p) {
        String cover = p.getImageUrls().isEmpty() ? null : p.getImageUrls().get(0);

        return new ProjectSummaryResponse(
                p.getSlug(),
                p.getTitle(),
                p.getSummary(),
                p.getPublishedAt(),
                List.copyOf(p.getTags()),
                List.copyOf(p.getStack()),
                p.getDemoUrl(),
                p.getRepoUrl(),
                cover
        );
    }

    public static ProjectDetailResponse toDetail(Project p) {
        return new ProjectDetailResponse(
                p.getSlug(),
                p.getTitle(),
                p.getSummary(),
                p.getDescription(),
                p.getPublishedAt(),
                List.copyOf(p.getTags()),
                List.copyOf(p.getStack()),
                List.copyOf(p.getImageUrls()),
                p.getDemoUrl(),
                p.getRepoUrl()
        );
    }
}
