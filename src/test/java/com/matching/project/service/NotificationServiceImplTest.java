package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Notification;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.repository.NotificationRepository;
import com.matching.project.repository.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Nested
    @DisplayName("알림 전송")
    class SendNotification {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long notificationNo = 2L;
            Type type = Type.PROJECT_PARTICIPATION_REFUSE;
            String receiver = "leeworld9@gmail.com";
            String title = "테스트 알림";
            String content = "상세내역";

            Long userNo = 1L;
            String userName = "관리자";
            String userEmail = "admin@admin.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            Long receiverUserNo = 2L;
            String receiverUserName = "테스터";
            String receiverUserEmail = "leeworld9@gmail.com";
            Role receiverUserRole = Role.ROLE_USER;

            User receiverUser = User.builder()
                    .no(receiverUserNo)
                    .name(receiverUserName)
                    .email(receiverUserEmail)
                    .permission(receiverUserRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            given(userRepository.findByEmail(receiver)).willReturn(Optional.ofNullable(receiverUser));

            //when
            Notification notification = notificationService.sendNotification(type, receiver, title, content);

            //then
            assertThat(notification.getType()).isEqualTo(type);
            assertThat(notification.getUser().getEmail()).isEqualTo(receiver);
            assertThat(notification.getTitle()).isEqualTo(title);
            assertThat(notification.getContent()).isEqualTo(content);
        }

        @DisplayName("실패 : 받는 사용자가 없는 경우")
        @Test
        void fail1() {
            //given
            Long notificationNo = 2L;
            Type type = Type.PROJECT_PARTICIPATION_REFUSE;
            String receiver = "leeworld9@gmail.com";
            String title = "테스트 알림";
            String content = "상세내역";

            Long userNo = 1L;
            String userName = "관리자";
            String userEmail = "admin@admin.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            given(userRepository.findByEmail(receiver)).willReturn(Optional.empty());

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Notification notification = notificationService.sendNotification(type, receiver, title, content);
            });


            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("This is not a registered email");
        }
    }

    @Nested
    @DisplayName("알림 리스트 조회")
    class NotificationList {
        @DisplayName("성공")
        @Test
        void success() {
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            List<Notification> notificationList = new ArrayList<>();
            for (int i = 0 ; i < 10 ; i++) {
                notificationList.add(Notification.builder()
                        .no(Integer.toUnsignedLong(i+1))
                        .type(Type.NOTICE)
                        .title("title" + Integer.toString(i+1))
                        .content("content" + Integer.toString(i+1))
                        .read(false)
                        .user(null)
                        .build()
                );
            }

            Long notificationNo = 3L;

            int page = 0;
            int size = 3;
            Pageable pageable = PageRequest.of(page, size, Sort.by("no").descending());
            int start = Math.toIntExact(notificationNo) - 1;
            int end = Math.min((start + pageable.getPageSize()), notificationList.size());
            boolean hasNext = notificationList.size() >= start + pageable.getPageSize() + 1;
            Slice<Notification> notifications = new SliceImpl<>(notificationList.subList(start, end), pageable, hasNext);

            given(notificationRepository.findByUserOrUserIsNullOrderByNoDescUsingPaging(user, notificationNo, pageable))
                    .willReturn(notifications);
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            SliceDto<NotificationSimpleInfoDto> dtos = notificationService.notificationList(notificationNo, pageable);

            //then
            assertThat(dtos.getContent().size()).isEqualTo(3);
            assertThat(dtos.getContent().get(0).getNo()).isEqualTo(Integer.toUnsignedLong(3));
            assertThat(dtos.getContent().get(0).getTitle()).isEqualTo("title" + 3);
            assertThat(dtos.getContent().get(1).getNo()).isEqualTo(Integer.toUnsignedLong(4));
            assertThat(dtos.getContent().get(1).getTitle()).isEqualTo("title" + 4);
            assertThat(dtos.getContent().get(2).getNo()).isEqualTo(Integer.toUnsignedLong(5));
            assertThat(dtos.getContent().get(2).getTitle()).isEqualTo("title" + 5);
            assertThat(dtos.isLast()).isFalse();

        }
    }

    @Nested
    @DisplayName("알림 상세 조회")
    class NotificationInfo {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long notificationNo = 2L;
            Type type = Type.PROJECT_PARTICIPATION_REFUSE;
            String receiver = "leeworld9@gmail.com";
            String title = "테스트 알림";
            String content = "상세내역";

            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            Notification notification =  Notification.builder()
                    .no(notificationNo)
                    .type(type)
                    .title(title)
                    .content(content)
                    .user(user)
                    .read(false)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            given(notificationRepository.findByNoWithUserUsingLeftFetchJoin(notificationNo)).willReturn(Optional.ofNullable(notification));

            //when
            NotificationDto dto = notificationService.notificationInfo(notificationNo);

            //then
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(dto.getTitle()).isEqualTo(title);
            assertThat(dto.getContent()).isEqualTo(content);
        }

        @DisplayName("실패 : 다른 사용자의 알림내역에 접근 (공지사항 제외)")
        @Test
        void fail1() {
            //given
            Long notificationNo = 2L;
            Type type = Type.PROJECT_PARTICIPATION_REFUSE;
            String receiver = "leeworld9@gmail.com";
            String title = "테스트 알림";
            String content = "상세내역";


            Long userNo = 1L;
            String userName = "테스터1";
            String userEmail = "leeworld9@github.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            Long receiverUserNo = 2L;
            String receiverUserName = "테스터2";
            String receiverUserEmail = "leeworld9@gmail.com";
            Role receiverUserRole = Role.ROLE_USER;

            User receiverUser = User.builder()
                    .no(receiverUserNo)
                    .name(receiverUserName)
                    .email(receiverUserEmail)
                    .permission(receiverUserRole)
                    .build();

            Notification notification =  Notification.builder()
                    .no(notificationNo)
                    .type(type)
                    .title(title)
                    .content(content)
                    .user(receiverUser)
                    .read(false)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            given(notificationRepository.findByNoWithUserUsingLeftFetchJoin(notificationNo)).willReturn(Optional.ofNullable(notification));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                NotificationDto dto = notificationService.notificationInfo(notificationNo);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Unauthorized User Access");

        }
    }
}