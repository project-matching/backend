package com.matching.project.controller;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.common.PasswordReissueRequestDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/common")
public class CommonController {
    
    @PostMapping("/login")
    @ApiOperation(value = "일반 로그인")
    public ResponseEntity<String> normalLogin(NormalLoginRequestDto normalLoginRequestDto) {

        return new ResponseEntity("로그인 완료되었습니다.", HttpStatus.OK);
    }
    
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
