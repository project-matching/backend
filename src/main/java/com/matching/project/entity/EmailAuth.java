package com.matching.project.entity;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class EmailAuth extends BaseTimeEntity{
    private static final Long MAX_EXPIRE_TIME = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String authToken;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EmailAuthPurpose purpose;

    @Column(nullable = false)
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
