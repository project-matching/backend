package com.matching.project.repository;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {
    @Query("select pp from ProjectPosition pp join fetch pp.position po join fetch pp.project p left join fetch pp.user where p= :project")
    List<ProjectPosition> findProjectAndPositionAndUserUsingFetchJoinByProjectNo(@Param("project") Project project);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    public void deleteByNoIn(Collection<Long> noList);
}
