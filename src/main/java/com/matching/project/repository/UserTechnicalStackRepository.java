package com.matching.project.repository;

import com.matching.project.entity.UserPosition;
import com.matching.project.entity.UserTechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTechnicalStackRepository extends JpaRepository<UserTechnicalStack, Long> {
    List<UserTechnicalStack> findAllByUserPosition(UserPosition userPosition);
    void deleteByUserPosition(UserPosition userPosition);
}
