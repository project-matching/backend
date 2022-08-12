package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSendRequestDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
import com.matching.project.entity.Notification;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(Type type, String receiver, String title, String content);
    SliceDto<NotificationSimpleInfoDto> notificationList(Long notificationNo, Pageable pageable);
    NotificationDto notificationInfo(Long notificationNo);
}
