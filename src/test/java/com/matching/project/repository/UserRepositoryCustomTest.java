package com.matching.project.repository;

import com.matching.project.config.JpaConfig;
import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.UserFilter;
import com.matching.project.dto.user.UserFilterDto;
import com.matching.project.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QuerydslConfiguration.class, JpaConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserRepositoryCustomTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepositoryCustom userRepositoryCustom;

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
    void findByNoUsingQueryDsl() {
        //given
        User user0 = saveUser("test0@xxxxxx.com");
        User user1 = saveUser("test1@xxxxxx.com");
        User user2 = saveUser("asb234@xxxxxx.com");
        User user3 = saveUser("test2@xxxxxx.com");
        User user4 = saveUser("23512s@xxxxxx.com");
        User user5 = saveUser("test3@xxxxxx.com");
        User user6 = saveUser("asdf2we@xxxxxx.com");


        UserFilterDto userFilterDto = UserFilterDto.builder()
                .userFilter(UserFilter.EMAIL)
                .content("test")
                .build();

        Pageable pageable = PageRequest.of(0, 2);

        //when
        Slice<User> users = userRepositoryCustom.findByNoOrderByNoDescUsingQueryDsl(Long.MAX_VALUE, userFilterDto, pageable).get();

        //then
        assertThat(users.getContent().size()).isEqualTo(2);
        assertThat(users.getContent().get(0).getEmail()).isEqualTo(user5.getEmail());
        assertThat(users.getContent().get(1).getEmail()).isEqualTo(user3.getEmail());
        assertThat(users.isLast()).isFalse();
    }
}