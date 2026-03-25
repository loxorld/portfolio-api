package com.brian.portfolioapi.config;

import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStage;
import com.brian.portfolioapi.model.ProjectStatus;
import com.brian.portfolioapi.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final ProjectRepository repo;

    public DataSeeder(ProjectRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return;
        }

        repo.saveAll(List.of(
                buildProject(
                        "cedica-app",
                        "CEDICA - Gestion institucional",
                        "App para centralizar legajos, turnos y seguimiento interno.",
                        "Proyecto de ejemplo para desarrollo local. Simula un sistema interno con foco en orden administrativo, trazabilidad y una API lista para consumir desde el frontend.",
                        Set.of("fullstack", "spring", "postgres"),
                        Set.of("Java", "Spring Boot", "PostgreSQL"),
                        List.of(),
                        "https://github.com/brian/cedica",
                        null,
                        ProjectStage.STABLE,
                        Instant.parse("2025-10-01T00:00:00Z")
                ),
                buildProject(
                        "volvi-a-casa",
                        "Volve a Casa - Mascotas perdidas",
                        "Sistema para publicar avisos, registrar reportes y seguir casos.",
                        "Proyecto de ejemplo para poblar la API en entorno local. Incluye un caso orientado a formularios, validaciones y gestion de estados desde un panel administrativo.",
                        Set.of("backend", "api", "jwt"),
                        Set.of("Java", "Spring", "JWT"),
                        List.of(),
                        "https://github.com/brian/volvi-a-casa",
                        null,
                        ProjectStage.IN_DEVELOPMENT,
                        Instant.parse("2025-12-10T00:00:00Z")
                )
        ));
    }

    private Project buildProject(
            String slug,
            String title,
            String summary,
            String description,
            Set<String> tags,
            Set<String> stack,
            List<String> images,
            String repoUrl,
            String demoUrl,
            ProjectStage stage,
            Instant publishedAt
    ) {
        Project p = new Project();
        p.setSlug(slug);
        p.setTitle(title);
        p.setSummary(summary);
        p.setDescription(description);
        p.setTags(new LinkedHashSet<>(tags));
        p.setStack(new LinkedHashSet<>(stack));
        p.setImageUrls(images);
        p.setRepoUrl(repoUrl);
        p.setDemoUrl(demoUrl);
        p.setStage(stage);
        p.setStatus(ProjectStatus.PUBLISHED);
        p.setPublishedAt(publishedAt);
        return p;
    }
}
