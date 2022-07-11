package com.matching.project.dto.user;

import lombok.*;

import javax.persistence.Column;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserBlockResponseDto {
    String email;
    private boolean block;
    private String blockReason;
}
