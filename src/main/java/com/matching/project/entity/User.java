package com.matching.project.entity;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
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

    @Column(nullable = true)
    private Long imageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_no")
    private Position position;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(permission.toString()));
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
