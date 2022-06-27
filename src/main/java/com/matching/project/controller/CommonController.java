package com.matching.project.controller;

import com.matching.project.dto.common.PasswordReissueRequestDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/common")
public class CommonController {

    private final UserDetailsService userDetailsService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    // TEST 용
    @ApiOperation(value = "현재 접속한 사용자 확인")
    @GetMapping("/info")
    public String getUserInfo() {
        return SecurityContextHolder.getContext().getAuthentication().toString();
    }

    // 일반 로그인 : spring security에서 처리하여 컨트롤러에서 처리할 필요가 없음.

    // 소셜 로그인
    
    @GetMapping("/logout")
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<String> logout() {
        return new ResponseEntity("로그아웃 완료되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 재발급")
    public ResponseEntity<String> passwordReissue(PasswordReissueRequestDto passwordReissueRequest) {
        return new ResponseEntity("로그아웃 완료되었습니다.", HttpStatus.OK);
    }
}
