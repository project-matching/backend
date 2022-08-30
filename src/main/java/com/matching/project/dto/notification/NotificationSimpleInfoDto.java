package com.matching.project.dto.notification;

import com.matching.project.dto.enumerate.Type;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class NotificationSimpleInfoDto {
    private Long notificationNo;
    private String title;
    private boolean read;
    private LocalDateTime createDate;
}
