package com.matching.project.repository;

import com.matching.project.entity.TechnicalStack;
import com.matching.project.entity.UserTechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicalStackRepository extends JpaRepository<TechnicalStack, Long> {
}
