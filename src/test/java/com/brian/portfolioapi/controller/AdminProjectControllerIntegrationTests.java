package com.brian.portfolioapi.controller;

import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStage;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProjectControllerIntegrationTests {

    private static final String ADMIN_TOKEN = "test-admin-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository repo;

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
    }

    @Test
    void createRequiresAdminToken() throws Exception {
        mockMvc.perform(post("/api/admin/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson("secure-project", ProjectStage.STABLE, publishedAt())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    void createPersistsProjectAndNormalizesSlug() throws Exception {
        mockMvc.perform(post("/api/admin/projects")
                        .header("X-Admin-Token", ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson("My-New-Project", ProjectStage.IN_DEVELOPMENT, publishedAt())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("my-new-project"))
                .andExpect(jsonPath("$.title").value("Project My-New-Project"))
                .andExpect(jsonPath("$.stage").value("IN_DEVELOPMENT"));

        Project saved = repo.findBySlug("my-new-project").orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
        assertThat(saved.getStage()).isEqualTo(ProjectStage.IN_DEVELOPMENT);
    }

    @Test
    void createRejectsInvalidAdminToken() throws Exception {
        mockMvc.perform(post("/api/admin/projects")
                        .header("X-Admin-Token", "wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson("blocked-project", ProjectStage.STABLE, publishedAt())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("Invalid admin token"));
    }

    @Test
    void listReturnsDraftsAndPublishedProjectsForAdmin() throws Exception {
        saveProject("published-project");
        saveDraftProject("draft-project");

        mockMvc.perform(get("/api/admin/projects")
                        .header("X-Admin-Token", ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].slug").value("draft-project"))
                .andExpect(jsonPath("$.items[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.items[0].stage").value("STABLE"))
                .andExpect(jsonPath("$.items[1].slug").value("published-project"))
                .andExpect(jsonPath("$.items[1].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.items[1].stage").value("IN_DEVELOPMENT"));
    }

    @Test
    void detailReturnsDraftProjectForAdmin() throws Exception {
        saveDraftProject("draft-project");

        mockMvc.perform(get("/api/admin/projects/{slug}", "draft-project")
                        .header("X-Admin-Token", ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("draft-project"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.stage").value("STABLE"))
                .andExpect(jsonPath("$.publishedAt").doesNotExist());
    }

    @Test
    void updateCanChangeSlugWhenTokenIsValid() throws Exception {
        saveProject("old-slug");

        mockMvc.perform(put("/api/admin/projects/{slug}", "old-slug")
                        .header("X-Admin-Token", ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson("new-slug", ProjectStage.STABLE, publishedAt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("new-slug"));

        assertThat(repo.findBySlug("new-slug")).isPresent();
        assertThat(repo.findBySlug("old-slug")).isEmpty();
    }

    @Test
    void deleteRemovesExistingProject() throws Exception {
        saveProject("project-to-delete");

        mockMvc.perform(delete("/api/admin/projects/{slug}", "project-to-delete")
                        .header("X-Admin-Token", ADMIN_TOKEN))
                .andExpect(status().isNoContent());

        assertThat(repo.findBySlug("project-to-delete")).isEmpty();
    }

    private String sampleRequestJson(String slug, ProjectStage stage, Instant publishedAt) {
        return """
                {
                  "slug": "%s",
                  "title": "Project %s",
                  "summary": "Summary %s",
                  "description": "Description %s",
                  "tags": ["backend", "portfolio"],
                  "stack": ["Java", "Spring Boot"],
                  "imageUrls": ["https://img.example/%s.png"],
                  "repoUrl": "https://github.com/loxorld/%s",
                  "demoUrl": "https://demo.example/%s",
                  "stage": "%s",
                  "publishedAt": "%s"
                }
                """.formatted(slug, slug, slug, slug, slug, slug, slug, stage, publishedAt);
    }

    private Instant publishedAt() {
        return Instant.parse("2025-04-01T10:00:00Z");
    }

    private void saveProject(String slug) {
        Project project = new Project();
        project.setSlug(slug);
        project.setTitle("Existing " + slug);
        project.setSummary("Existing summary " + slug);
        project.setDescription("Existing description " + slug);
        project.setStage(ProjectStage.IN_DEVELOPMENT);
        project.setStatus(ProjectStatus.PUBLISHED);
        project.setPublishedAt(Instant.parse("2025-03-15T10:00:00Z"));
        project.setTags(new LinkedHashSet<>(Set.of("existing")));
        project.setStack(new LinkedHashSet<>(Set.of("Java")));
        project.setImageUrls(List.of("https://img.example/" + slug + ".png"));
        repo.save(project);
    }

    private void saveDraftProject(String slug) {
        Project project = new Project();
        project.setSlug(slug);
        project.setTitle("Draft " + slug);
        project.setSummary("Draft summary " + slug);
        project.setDescription("Draft description " + slug);
        project.setStatus(ProjectStatus.DRAFT);
        project.setTags(new LinkedHashSet<>(Set.of("draft")));
        project.setStack(new LinkedHashSet<>(Set.of("Java")));
        project.setImageUrls(List.of());
        repo.save(project);
    }
}
