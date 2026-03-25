package com.brian.portfolioapi.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "projects",
        indexes = {
                @Index(name = "idx_projects_slug", columnList = "slug", unique = true),
                @Index(name = "idx_projects_status_publishedAt", columnList = "status,publishedAt")
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String slug;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank
    @Size(max = 220)
    @Column(nullable = false, length = 220)
    private String summary;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(length = 24)
    private ProjectStage stage = ProjectStage.STABLE;

    private Instant publishedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag", nullable = false, length = 40)
    private Set<String> tags = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_stack", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech", nullable = false, length = 60)
    private Set<String> stack = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_images", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "url", nullable = false, length = 400)
    private List<String> imageUrls = new ArrayList<>();

    @Size(max = 400)
    @Column(length = 400)
    private String repoUrl;

    @Size(max = 400)
    @Column(length = 400)
    private String demoUrl;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.stage = getStage();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.stage = getStage();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ProjectStage getStage() {
        return stage == null ? ProjectStage.STABLE : stage;
    }

    public void setStage(ProjectStage stage) {
        this.stage = stage == null ? ProjectStage.STABLE : stage;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getStack() {
        return stack;
    }

    public void setStack(Set<String> stack) {
        this.stack = stack;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }
}
