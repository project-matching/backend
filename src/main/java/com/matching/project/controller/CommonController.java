package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.common.PasswordReissueRequestDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.entity.User;
import com.matching.project.service.CommonService;
import com.matching.project.service.JwtTokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/common")
public class CommonController {

    private final CommonService commonService;
    private final JwtTokenService jwtTokenService;

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
        return new ResponseEntity("로그아웃 완료되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/password/reissue")
    @ApiOperation(value = "비밀번호 재발급")
    public ResponseEntity<String> passwordReissue(PasswordReissueRequestDto passwordReissueRequest) {
        return new ResponseEntity("로그아웃 완료되었습니다.", HttpStatus.OK);
    }
}
