package com.matching.project.repository;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    @Query("select pu from ProjectUser pu join fetch pu.user u join fetch pu.project p where p.no = :projectNo")
    public List<ProjectUser> findByProjectNo(@Param("projectNo") Long projectNo);
}
