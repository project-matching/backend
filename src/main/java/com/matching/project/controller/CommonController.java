package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.*;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.repository.EmailAuthRepository;
import com.matching.project.service.CommonService;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/common")
public class CommonController {

    private final CommonService commonService;
    private final JwtTokenService jwtTokenService;
    private final EmailService emailService;

    // 일반 로그인
    @PostMapping("/login")
    @ApiOperation(value = "일반 로그인")
    public ResponseEntity normalLogin(@RequestBody NormalLoginRequestDto dto) {
        try {
            User user = commonService.normalLogin(dto);
            TokenDto tokenDto = TokenDto.builder()
                    .no(user.getNo())
                    .email(user.getEmail())
                    .build();
            String jwtAccessToken = jwtTokenService.createToken(tokenDto);
            ResponseDto<String> response = ResponseDto.<String>builder().data(jwtAccessToken).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDto<String> response = ResponseDto.<String>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 소셜 로그인
    @GetMapping("/logout")
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<String> logout() {
        //프론트에서 저장된 jwt 토큰 제거도 필요
        SecurityContextHolder.clearContext();
        ResponseDto<String> response = ResponseDto.<String>builder().data("Success Logout").build();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 재발급 이메일 요청")
    public ResponseEntity passwordReissueCall(@RequestBody PasswordReissueCallRequestDto dto) {
        try {
            dto.setPurpose(EmailAuthPurpose.PASSWORD_REISSUE);

            EmailAuth emailAuth = emailService.beforeSendWork(dto.getEmail(), dto.getPurpose());
            emailService.sendPasswordReissueEmail(dto.getEmail(), emailAuth.getAuthToken());

            ResponseDto<String> response = ResponseDto.<String>builder().data("패스워드 재발급 요청이 이메일로 전송되었습니다.").build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDto<String> response = ResponseDto.<String>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 재발급 (토큰 유효성 체크 포함)")
    public ResponseEntity passwordReissue(PasswordReissueRequestDto dto) {
        try {
            dto.setPurpose(EmailAuthPurpose.PASSWORD_REISSUE);

            String newPassword = emailService.CheckPasswordReissueEmail(dto);
            PasswordReissueResponseDto reissueResponseDto = PasswordReissueResponseDto.builder()
                    .email(dto.getEmail())
                    .password(newPassword)
                    .build();

            ResponseDto<PasswordReissueResponseDto> response = ResponseDto.<PasswordReissueResponseDto>builder().data(reissueResponseDto).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ResponseDto<String> response = ResponseDto.<String>builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
