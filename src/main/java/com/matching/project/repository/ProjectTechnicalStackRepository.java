
package com.matching.project.repository;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectTechnicalStack;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectTechnicalStackRepository extends JpaRepository<ProjectTechnicalStack, Long> {
    @Query("select pts from ProjectTechnicalStack pts join fetch pts.technicalStack ts join fetch pts.project p where p = :project")
    public List<ProjectTechnicalStack> findTechnicalStackAndProjectUsingFetchJoin(@Param("project") Project project);
}
