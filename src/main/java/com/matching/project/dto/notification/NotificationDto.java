package com.matching.project.dto.notification;

import com.matching.project.dto.enumerate.Type;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Builder
@Data
public class NotificationDto {
    private Type type;

    private String title;

    private String content;

    private LocalDateTime createDate;
}
