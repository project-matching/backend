package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EmailAuthRequestDto {
    String email;
    String authToken;
    EmailAuthPurpose purpose;
}
