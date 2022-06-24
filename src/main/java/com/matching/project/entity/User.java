package com.matching.project.entity;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class User {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 1, nullable = false)
    private char sex;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 255)
    private String github;

    @Column(length = 255)
    private String selfIntroduction;

    private boolean block;

    @Column(length = 255)
    private String block_reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role permission;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private OAuth oauthCategory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_no")
    private Image image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_position_no")
    private UserPosition userPosition;
}
