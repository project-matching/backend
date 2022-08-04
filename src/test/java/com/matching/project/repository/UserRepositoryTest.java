package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    User saveUser(String email) {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email(email)
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .imageNo(null)
                .position(null)
                .build();
        return userRepository.save(user);
    }

    @Test
    void findByEmail() {
        //given
        User user = saveUser("test@test.com");

        //when
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(optionalUser.get().getNo()).isEqualTo(user.getNo());
        assertThat(optionalUser.get().getEmail()).isEqualTo(user.getEmail());

    }
}