package com.matching.project.repository;

import com.matching.project.entity.ProjectUser;
import com.matching.project.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
}
