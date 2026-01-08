package com.brian.portfolioapi.repository;

import com.brian.portfolioapi.model.Project;
import com.brian.portfolioapi.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findBySlugAndStatus(String slug, ProjectStatus status);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("""
        select p
        from Project p
        where p.status = :status
          and exists (
            select 1
            from Project p2 join p2.tags t
            where p2 = p and lower(t) = lower(:tag)
          )
        """)
    Page<Project> findByStatusAndTag(ProjectStatus status, String tag, Pageable pageable);

    Optional<Project> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
