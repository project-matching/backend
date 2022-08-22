package com.matching.project.dto.token;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TokenDto {
    String accessToken;
    String refreshToken;
}
