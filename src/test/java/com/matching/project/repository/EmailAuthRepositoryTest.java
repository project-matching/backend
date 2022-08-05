package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.entity.EmailAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmailAuthRepositoryTest {

    @Autowired
    EmailAuthRepository emailAuthRepository;

    @Test
    void findByEmailAndAuthTokenAndPurpose() {
        //given
        String email = "test@test.com";
        String authToken = "askl13rlkfj12fklaopsf";
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        EmailAuth auth = EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        emailAuthRepository.save(auth);

        //when
        Optional<EmailAuth> optionalEmailAuth = emailAuthRepository.findByEmailAndAuthTokenAndPurpose(email, authToken, purpose);

        //then
        assertThat(optionalEmailAuth.get().getEmail()).isEqualTo(email);
        assertThat(optionalEmailAuth.get().getAuthToken()).isEqualTo(authToken);
        assertThat(optionalEmailAuth.get().getPurpose().toString()).isEqualTo(purpose.toString());

    }

    @Test
    void deleteAllByEmailAndPurpose() {
        //given
        String email = "test@test.com";
        String authToken = "askl13rlkfj12fklaopsf";
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        EmailAuth auth = EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        emailAuthRepository.save(auth);

        //when
        emailAuthRepository.deleteAllByEmailAndPurpose(email, purpose);

        //then
        List<EmailAuth> authList = emailAuthRepository.findAll();
        assertThat(authList.size()).isEqualTo(0);
    }
}