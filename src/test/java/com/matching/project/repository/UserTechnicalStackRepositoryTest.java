package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Image;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.entity.User;
import com.matching.project.entity.UserTechnicalStack;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserTechnicalStackRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    UserTechnicalStackRepository userTechnicalStackRepository;

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

    List<TechnicalStack> saveTechnicalStack() {
        TechnicalStack t1 = technicalStackRepository.save(TechnicalStack.builder().name("Spring").build());
        TechnicalStack t2 = technicalStackRepository.save(TechnicalStack.builder().name("React").build());
        TechnicalStack t3 = technicalStackRepository.save(TechnicalStack.builder().name("JPA").build());
        return List.of(t1, t2, t3);
    }

    @Test
    void findUserTechnicalStacksByUser() {
        //given
        User user1 = saveUser("test1@test.com");
        User user2 = saveUser("test2@test.com");
        List<TechnicalStack> t = saveTechnicalStack();

        UserTechnicalStack ust1 = UserTechnicalStack.builder()
                .technicalStack(t.get(0))
                .user(user1)
                .build();
        userTechnicalStackRepository.save(ust1);

        UserTechnicalStack ust2 = UserTechnicalStack.builder()
                .technicalStack(t.get(1))
                .user(user1)
                .build();
        userTechnicalStackRepository.save(ust2);

        UserTechnicalStack ust3 = UserTechnicalStack.builder()
                .technicalStack(t.get(2))
                .user(user2)
                .build();
        userTechnicalStackRepository.save(ust3);

        //when
        List<UserTechnicalStack> userTechnicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(user1.getNo());

        //then
        assertThat(userTechnicalStackList.size()).isEqualTo(2);
        assertThat(userTechnicalStackList.get(0).getTechnicalStack()).isEqualTo(ust1.getTechnicalStack());
        assertThat(userTechnicalStackList.get(0).getUser().getName()).isEqualTo(ust1.getUser().getName());
        assertThat(userTechnicalStackList.get(1).getTechnicalStack()).isEqualTo(ust2.getTechnicalStack());
        assertThat(userTechnicalStackList.get(1).getUser().getName()).isEqualTo(ust2.getUser().getName());
    }

    @Test
    void deleteAllByUser() {
        //given
        User user = saveUser("test1@test.com");
        List<TechnicalStack> t = saveTechnicalStack();

        UserTechnicalStack ust1 = UserTechnicalStack.builder()
                .technicalStack(t.get(0))
                .user(user)
                .build();
        userTechnicalStackRepository.save(ust1);

        //when
        userTechnicalStackRepository.deleteAllByUser(user);

        //then
        List<UserTechnicalStack> ust = userTechnicalStackRepository.findAll();
        assertThat(ust.size()).isEqualTo(0);
    }
}