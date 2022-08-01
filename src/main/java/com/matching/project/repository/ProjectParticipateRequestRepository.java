package com.matching.project.repository;

import com.matching.project.entity.ProjectParticipateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectParticipateRequestRepository extends JpaRepository<ProjectParticipateRequest, Long> , ProjectParticipateRequestCustom{
}
