package com.matching.project.dto.common;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TokenDto {
    Long no;
    String email;
}
