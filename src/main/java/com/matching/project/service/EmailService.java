package com.matching.project.service;

import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailService {
    EmailAuth emailAuthSave(String email);
    User confirmEmail(EmailAuthRequestDto emailAuthRequestDto);
    void sendConfirmEmail(String email, String authToken);
    void emailAuthReSend(String email);
}
