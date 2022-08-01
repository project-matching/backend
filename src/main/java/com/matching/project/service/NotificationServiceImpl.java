package com.matching.project.service;

import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSendRequestDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
import com.matching.project.entity.Notification;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.NotificationRepository;
import com.matching.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public Notification sendNotification(Type type, String receiver, String title, String content) {
        User receiveUser = null;
        // 공지사항은 받는 유저를 null 로 셋팅
        if (type != Type.NOTICE) {
            Optional<User> optionalUser = userRepository.findByEmail(receiver);
            optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));
            receiveUser = optionalUser.get();
        }

        Notification notification = Notification.builder()
                .type(type)
                .read(false)
                .title(title)
                .content(content)
                .user(receiveUser)
                .build();

        notificationRepository.save(notification);
        return notification;
    }

    @Override
    public List<NotificationSimpleInfoDto> notificationList(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());
        
        // 공지사항도 같이 조회하기 위해서 User가 null인 경우도 조회
        List<Notification> notificationList = notificationRepository.findByUserOrUserIsNullOrderByNoDesc(optionalUser.get(), pageable);

        return notificationList.stream().map(notification -> NotificationSimpleInfoDto.builder()
                        .no(notification.getNo())
                        .type(notification.getType())
                        .title(notification.getTitle())
                        .read(notification.isRead())
                        .createDate(notification.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public NotificationDto notificationInfo(Long notificationNo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Notification> optionalNotification = notificationRepository.findByNoWithUserUsingLeftFetchJoin(notificationNo);
        optionalNotification.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_NOTIFICATION_NO_EXCEPTION));

        // Valid Check
        if (optionalNotification.get().getUser() != null) {
            // 받는 사람만 알람에 접근할 수 있어야함. (공지사항 제외)
            if (optionalNotification.get().getUser().getNo() != optionalUser.get().getNo())
                throw new CustomException(ErrorCode.UNAUTHORIZED_USER_ACCESS_EXCEPTION);
        }

        // 상세내역 확인 시 읽음 처리 실시
        optionalNotification.get().readProcessing();

        return NotificationDto.builder()
                .type(optionalNotification.get().getType())
                .title(optionalNotification.get().getTitle())
                .content(optionalNotification.get().getContent())
                .createDate(optionalNotification.get().getCreatedDate())
                .build();
    }
}
