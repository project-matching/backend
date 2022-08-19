package com.matching.project.repository;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {
    @Query("select pp from ProjectPosition pp join fetch pp.position po join fetch pp.project p left join fetch pp.user where p= :project")
    Optional<List<ProjectPosition>> findProjectAndPositionAndUserUsingFetchJoinByProject(@Param("project") Project project);

    @Query("select pp from ProjectPosition pp left join fetch pp.user where pp.no = :projectPositionNo")
    Optional<ProjectPosition> findUserAndProjectFetchJoinByProjectPositionNo(@Param("projectPositionNo") Long projectPositionNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    public void deleteByNoIn(Collection<Long> noList);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProjectPosition pp where pp.project.no = :projectNo")
    public void deleteByProjectNo(@Param("projectNo") Long projectNo);
}
