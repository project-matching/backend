package com.matching.project.repository;

import com.matching.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>{

    @Query("select p from Project p where p.state = :state and p.delete = :delete")
    Page<Project> findByStateProjectPage(@Param("state") boolean state, @Param("delete") boolean delete, Pageable pageable);
}
