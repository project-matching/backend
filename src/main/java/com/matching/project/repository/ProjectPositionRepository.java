package com.matching.project.repository;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {
    @Query("select pp from ProjectPosition pp left join fetch pp.position po left join fetch pp.project p left join fetch pp.user where p= :project")
    List<ProjectPosition> findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(@Param("project") Project project);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProjectPosition pp where pp.project = :project and pp.user is null")
    void deleteByProjectAndUserIsNull(@Param("project") Project project);
}
