package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.entity.Notification;
import com.matching.project.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class NotificationRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NotificationRepository notificationRepository;

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
    void findByNoWithUserUsingLeftFetchJoin() {
        //given
        User user1 = saveUser("test1@test.com");

        Notification notification1 = Notification.builder()
                .user(user1)
                .title("title1")
                .content("content1")
                .read(false)
                .type(Type.PROJECT_PARTICIPATION_REQUEST)
                .build();
        notificationRepository.save(notification1);


        //when
        Optional<Notification> notification = notificationRepository.findByNoWithUserUsingLeftFetchJoin(notification1.getNo());

        //then
        assertThat(notification.get().getUser().getName()).isEqualTo(user1.getName());
        assertThat(notification.get().getTitle()).isEqualTo(notification1.getTitle());
        assertThat(notification.get().getContent()).isEqualTo(notification1.getContent());
        assertThat(notification.get().getType().toString()).isEqualTo(notification1.getType().toString());
        assertThat(notification.get().isRead()).isFalse();
    }

    @Test
    void findByUserOrUserIsNullOrderByNoDescUsingPaging() {
        //given
        User user1 = saveUser("test1@test.com");
        User user2 = saveUser("test2@test.com");

        Notification notification1 = Notification.builder()
                .user(user2)
                .title("title2")
                .content("content2")
                .read(false)
                .type(Type.PROJECT_PARTICIPATION_REQUEST)
                .build();
        notificationRepository.save(notification1);

        Notification notification2 = Notification.builder()
                .user(null)
                .title("notice_title")
                .content("notice_content")
                .read(false)
                .type(Type.NOTICE)
                .build();
        notificationRepository.save(notification2);

        Notification notification3 = Notification.builder()
                .user(user2)
                .title("title3")
                .content("content3")
                .read(false)
                .type(Type.PROJECT_DELETE)
                .build();
        notificationRepository.save(notification3);

        Notification notification4 = Notification.builder()
                .user(user1)
                .title("title1")
                .content("content1")
                .read(false)
                .type(Type.PROJECT_PARTICIPATION_REFUSE)
                .build();
        notificationRepository.save(notification4);

        Notification notification5 = Notification.builder()
                .user(user2)
                .title("title5")
                .content("content5")
                .read(false)
                .type(Type.PROJECT_PARTICIPATION_REFUSE)
                .build();
        notificationRepository.save(notification5);


        Pageable pageable = PageRequest.of(0, 2);

        //when
        Slice<Notification> notificationList = notificationRepository.findByUserOrUserIsNullOrderByNoDescUsingPaging(user2, notification4.getNo(), pageable).get();

        //then
        assertThat(notificationList.getContent().size()).isEqualTo(2);
        assertThat(notificationList.getContent().get(0).getTitle()).isEqualTo(notification3.getTitle());
        assertThat(notificationList.getContent().get(0).getType().toString()).isEqualTo(notification3.getType().toString());
        assertThat(notificationList.getContent().get(1).getTitle()).isEqualTo(notification2.getTitle());
        assertThat(notificationList.getContent().get(1).getType().toString()).isEqualTo(notification2.getType().toString());
        assertThat(notificationList.isLast()).isFalse();
    }
}