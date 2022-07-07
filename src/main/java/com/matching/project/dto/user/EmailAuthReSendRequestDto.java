package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EmailAuthReSendRequestDto {
    String email;
    EmailAuthPurpose purpose;
}
