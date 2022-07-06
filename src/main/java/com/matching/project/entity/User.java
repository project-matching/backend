package com.matching.project.entity;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.UserSimpleInfoDto;
import com.matching.project.dto.user.UserUpdateRequestDto;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

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
    private String blockReason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role permission;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private OAuth oauthCategory;

    private boolean email_auth;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_no")
    private Image image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_position_no")
    private UserPosition userPosition;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(permission.toString()));
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String newPassword) {

        if (!"".equals(newPassword) && newPassword != null)
            this.password = passwordEncoder.encode(newPassword);
    }

    public User updateUser(UserUpdateRequestDto dto, UserPosition userPosition) {
        this.name = dto.getName();
        this.sex = dto.getSex().charAt(0);
        this.github = dto.getGithub();
        this.selfIntroduction = dto.getSelfIntroduction();
        this.image = null;
        this.userPosition = userPosition;
        return this;
    }

    public void passwordReIssue(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void emailVerifiedSuccess() {
        this.email_auth = true;
    }

}
