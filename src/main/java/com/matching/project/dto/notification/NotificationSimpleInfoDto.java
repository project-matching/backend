package com.matching.project.dto.notification;

import com.matching.project.dto.enumerate.Type;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NotificationSimpleInfoDto {
    private Long no;
    private Type sender;
    private String title;
    private LocalDateTime createDate;
}
