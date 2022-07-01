package com.matching.project.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailAuthRequestDto {
    String email;
    String authToken;
}
