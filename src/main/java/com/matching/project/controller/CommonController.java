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
    @ApiOperation(value = "일반 로그인 (수정 완료)")
    public ResponseEntity<ResponseDto<String>> normalLogin(@RequestBody NormalLoginRequestDto dto) {
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
            ResponseDto<String> response = ResponseDto.<String>builder().error(null).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 소셜 로그인
    @GetMapping("/logout")
    @ApiOperation(value = "로그아웃 (추후 수정 필요)")
    public ResponseEntity<String> logout() {
        //프론트에서 저장된 jwt 토큰 제거도 필요
        SecurityContextHolder.clearContext();
        ResponseDto<String> response = ResponseDto.<String>builder().data("Success Logout").build();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    // request dto 수정 필요
    @PostMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 변경 이메일 요청 (request dto 수정 필요)")
    public ResponseEntity<ResponseDto<Boolean>> passwordReissueCall(@RequestBody PasswordReissueCallRequestDto dto) {
        dto.setPurpose(EmailAuthPurpose.PASSWORD_REISSUE);

        EmailAuth emailAuth = emailService.beforeSendWork(dto.getEmail(), dto.getPurpose());
        emailService.sendPasswordReissueEmail(dto.getEmail(), emailAuth.getAuthToken());

        return ResponseEntity.ok(new ResponseDto<Boolean>(null, true));
    }

    // request dto 수정 필요
    @GetMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 변경 페이지 (request dto 수정 필요)")
    public ResponseEntity<ResponseDto<String>> passwordReissue(PasswordReissueRequestDto dto) {
        dto.setPurpose(EmailAuthPurpose.PASSWORD_REISSUE);

        String newPassword = emailService.CheckPasswordReissueEmail(dto);
        PasswordReissueResponseDto reissueResponseDto = PasswordReissueResponseDto.builder()
                .email(dto.getEmail())
                .password(newPassword)
                .build();

        //TODO
        //JWT
        return ResponseEntity.ok(new ResponseDto<String>(null, "jwt"));

    }
}