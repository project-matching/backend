package com.matching.project.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationSendRequestDto {
    @NotBlank
    String title;

    @NotBlank
    String content;
}
