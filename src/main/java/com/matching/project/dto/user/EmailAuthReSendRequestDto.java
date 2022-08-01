package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EmailAuthReSendRequestDto {
    @NotBlank
    String email;
}
