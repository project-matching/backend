package com.matching.project.repository;

import com.matching.project.entity.UserTechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTechnicalStackRepository extends JpaRepository<UserTechnicalStack, Long> {
}
