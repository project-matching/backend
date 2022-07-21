package com.matching.project.dto.common;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PasswordReissueRequestDto {
    String email;
    String password;
    String authToken;
    EmailAuthPurpose purpose;
}
