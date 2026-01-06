package com.brian.portfolioapi.config;

import com.brian.portfolioapi.model.Project;
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
        // Evita duplicar si reiniciás
        if (repo.count() > 0) return;

        repo.saveAll(List.of(
                buildProject(
                        "cedica-app",
                        "CEDICA – Gestión institucional",
                        "App web fullstack para gestión de terapias asistidas con caballos.",
                        "Descripción larga en markdown o texto plano (después la mejoramos).",
                        Set.of("fullstack", "spring", "postgres"),
                        Set.of("Java", "Spring Boot", "PostgreSQL"),
                        List.of("https://picsum.photos/seed/cedica/1200/800"),
                        "https://github.com/brian/cedica",
                        null,
                        Instant.parse("2025-10-01T00:00:00Z")
                ),
                buildProject(
                        "volvi-a-casa",
                        "Volvé a Casa – Mascotas perdidas",
                        "Sistema para reportar y encontrar mascotas con panel y autenticación.",
                        "Descripción larga del proyecto…",
                        Set.of("backend", "api", "jwt"),
                        Set.of("Java", "Spring", "JWT"),
                        List.of("https://picsum.photos/seed/volvi/1200/800"),
                        "https://github.com/brian/volvi-a-casa",
                        null,
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
        p.setStatus(ProjectStatus.PUBLISHED);
        p.setPublishedAt(publishedAt);
        return p;
    }
}
