package com.matching.project.repository;

import com.matching.project.entity.Comment;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository  extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findByEmailAndAndAuthToken(String email, String authToken);
    Optional<EmailAuth> deleteAllByEmail(String email);
}
