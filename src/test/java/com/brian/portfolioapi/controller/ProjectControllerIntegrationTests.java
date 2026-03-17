package com.brian.portfolioapi.controller;

import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository repo;

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
    }

    @Test
    void listPublishedProjectsReturnsOnlyPublishedProjectsSortedByDate() throws Exception {
        saveProject(
                "older-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-01-10T10:00:00Z"),
                Set.of("java", "spring"),
                Set.of("Java", "Spring Boot")
        );
        saveProject(
                "draft-project",
                ProjectStatus.DRAFT,
                null,
                Set.of("internal"),
                Set.of("Java")
        );
        saveProject(
                "newer-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-02-10T10:00:00Z"),
                Set.of("next"),
                Set.of("TypeScript", "Next.js")
        );

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].slug").value("newer-project"))
                .andExpect(jsonPath("$.items[1].slug").value("older-project"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void listPublishedProjectsCanBeFilteredByTagIgnoringCase() throws Exception {
        saveProject(
                "api-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-03-01T12:00:00Z"),
                Set.of("spring", "backend"),
                Set.of("Java", "Spring Boot")
        );
        saveProject(
                "frontend-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-03-02T12:00:00Z"),
                Set.of("next"),
                Set.of("TypeScript", "Next.js")
        );

        mockMvc.perform(get("/api/projects").param("tag", "SPRING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].slug").value("api-project"));
    }

    @Test
    void listPublishedProjectsSupportsWhitelistedSortFields() throws Exception {
        saveProject(
                "zeta-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-03-01T12:00:00Z"),
                Set.of("backend"),
                Set.of("Java")
        );
        saveProject(
                "alpha-project",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-03-02T12:00:00Z"),
                Set.of("frontend"),
                Set.of("TypeScript")
        );

        mockMvc.perform(get("/api/projects").param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].slug").value("alpha-project"))
                .andExpect(jsonPath("$.items[1].slug").value("zeta-project"));
    }

    @Test
    void listPublishedProjectsRejectsUnknownSortField() throws Exception {
        mockMvc.perform(get("/api/projects").param("sort", "createdAt,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Unsupported sort field: createdAt. Allowed values: publishedAt, title, slug"));
    }

    @Test
    void listPublishedProjectsRejectsUnknownSortDirection() throws Exception {
        mockMvc.perform(get("/api/projects").param("sort", "slug,sideways"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Unsupported sort direction: sideways. Allowed values: asc, desc"));
    }

    @Test
    void detailReturnsPublishedProjectBySlug() throws Exception {
        saveProject(
                "portfolio-api",
                ProjectStatus.PUBLISHED,
                Instant.parse("2025-03-05T15:30:00Z"),
                Set.of("spring"),
                Set.of("Java", "PostgreSQL")
        );

        mockMvc.perform(get("/api/projects/{slug}", "portfolio-api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("portfolio-api"))
                .andExpect(jsonPath("$.title").value("Title portfolio-api"))
                .andExpect(jsonPath("$.stack", hasSize(2)));
    }

    @Test
    void detailReturnsNotFoundForDraftProjects() throws Exception {
        saveProject(
                "hidden-project",
                ProjectStatus.DRAFT,
                null,
                Set.of("internal"),
                Set.of("Java")
        );

        mockMvc.perform(get("/api/projects/{slug}", "hidden-project"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    private void saveProject(
            String slug,
            ProjectStatus status,
            Instant publishedAt,
            Set<String> tags,
            Set<String> stack
    ) {
        Project project = new Project();
        project.setSlug(slug);
        project.setTitle("Title " + slug);
        project.setSummary("Summary " + slug);
        project.setDescription("Description " + slug);
        project.setStatus(status);
        project.setPublishedAt(publishedAt);
        project.setTags(new LinkedHashSet<>(tags));
        project.setStack(new LinkedHashSet<>(stack));
        project.setImageUrls(List.of("https://img.example/" + slug + ".png"));
        project.setRepoUrl("https://github.com/loxorld/" + slug);
        project.setDemoUrl("https://demo.example/" + slug);
        repo.save(project);
    }
}
