package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.*;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.token.TokenDto;
import com.matching.project.dto.token.TokenReissueRequestDto;
import com.matching.project.dto.token.TokenReissueResponseDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.service.CommonService;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    @ApiOperation(value = "일반 로그인")
    public ResponseEntity<ResponseDto<TokenDto>> normalLogin(@RequestBody @Valid NormalLoginRequestDto dto) {
        User user = commonService.normalLogin(dto);

        // Jwt Token Create
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(user.getEmail())
                .build();
        TokenDto tokenDto = jwtTokenService.createToken(tokenClaimsDto);

        // refresh Token save
        jwtTokenService.setRefreshToken(user.getEmail(), tokenDto.getRefresh());

        return ResponseEntity.ok().body(new ResponseDto<>(null, tokenDto));
    }

    @GetMapping("/logout")
    @ApiOperation(value = "로그아웃")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Boolean>> logout(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.replaceAll("^Bearer( )*", "");
        commonService.userLogout(accessToken);
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, true));
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
        TokenDto tokenDto = jwtTokenService.createToken(tokenClaimsDto);

        // refresh Token save
        jwtTokenService.setRefreshToken(user.getEmail(), tokenDto.getRefresh());

        return ResponseEntity.ok().body(new ResponseDto<>(null, tokenDto));
    }

    @PostMapping("/token/reissue")
    @ApiOperation(value = "JWT 토큰 재발급 요청")
    public ResponseEntity<ResponseDto<TokenReissueResponseDto>> jwtAccessTokenReissue(@RequestBody @Valid TokenReissueRequestDto dto) {

        // Jwt Access Token Reissue
        TokenReissueResponseDto tokenReissueResponseDto = jwtTokenService.accessTokenReissue(dto);

        return ResponseEntity.ok().body(new ResponseDto<>(null, tokenReissueResponseDto));
    }
}