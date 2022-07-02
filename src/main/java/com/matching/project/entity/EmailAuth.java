package com.matching.project.entity;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class EmailAuth {
    private static final Long MAX_EXPIRE_TIME = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String authToken;
    private EmailAuthPurpose purpose;
    private LocalDateTime expireDate;

    @Builder
    public EmailAuth(String email, String authToken, EmailAuthPurpose purpose) {
        this.email = email;
        this.authToken = authToken;
        this.purpose = purpose;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }

    public void setExpireTimeForTest(LocalDateTime time){
        this.expireDate = time;
    }
}
