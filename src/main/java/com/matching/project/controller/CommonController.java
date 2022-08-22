package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.*;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.token.TokenDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
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

import javax.validation.Valid;

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
    public ResponseEntity<ResponseDto<TokenDto>> normalLogin(@RequestBody @Valid NormalLoginRequestDto dto) {
        User user = commonService.normalLogin(dto);
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(user.getEmail())
                .build();
        return ResponseEntity.ok().body(new ResponseDto<>(null, jwtTokenService.createToken(tokenClaimsDto)));
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

    @PostMapping("/password/init")
    @ApiOperation(value = "비밀번호 초기화 이메일 요청")
    public ResponseEntity<ResponseDto<Boolean>> passwordInitCall(@RequestBody @Valid PasswordInitCallRequestDto dto) {
        EmailAuth emailAuth = emailService.beforeSendWork(dto.getEmail(), EmailAuthPurpose.PASSWORD_INIT);
        emailService.sendPasswordInitEmail(dto.getEmail(), emailAuth.getAuthToken());
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, true));
    }

    @PatchMapping("/password/confirm")
    @ApiOperation(value = "비밀번호 초기화 페이지")
    public ResponseEntity<ResponseDto<TokenDto>> passwordInit(@RequestBody @Valid PasswordInitRequestDto dto) {
        User user = emailService.checkPasswordInitEmail(dto, EmailAuthPurpose.PASSWORD_INIT);

        // Jwt Token Create
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(user.getEmail())
                .build();
        return ResponseEntity.ok().body(new ResponseDto<>(null, jwtTokenService.createToken(tokenClaimsDto)));
    }
}