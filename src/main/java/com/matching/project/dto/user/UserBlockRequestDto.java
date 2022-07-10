package com.matching.project.dto.user;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserBlockRequestDto {
    private String blockReason;
}
