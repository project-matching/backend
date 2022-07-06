package com.matching.project.repository;

import com.matching.project.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    void deleteByNo(Long no);
}
