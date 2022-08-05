package com.matching.project.repository;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository  extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findByEmailAndAuthTokenAndPurpose(String email, String authToken, EmailAuthPurpose purpose);
    void deleteAllByEmailAndPurpose(String email, EmailAuthPurpose purpose);
}
