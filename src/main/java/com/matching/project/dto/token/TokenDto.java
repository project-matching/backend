package com.matching.project.dto.token;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TokenDto {
    String access;
    String access_exp;
    String refresh;
    //String refresh_exp;
}
