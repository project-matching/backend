package com.matching.project.dto.notification;

import com.matching.project.dto.enumerate.Type;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
@Getter
public class NotificationDto {
    private String content;

    private Long sender;

    private Long receiver;

    private Type type;

    private Long projectParticipateNo;
}
