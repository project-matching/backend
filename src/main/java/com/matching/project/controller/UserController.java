package com.matching.project.controller;

import com.matching.project.dto.user.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    // 회원가입
    @PostMapping
    @ApiOperation(value = "회원가입")
    public ResponseEntity<String> signUp(SignUpRequestDto signUpRequestDto) {
        return new ResponseEntity("회원가입 완료되었습니다.", HttpStatus.OK);
    }
    
    // 회원 정보 조회
    @GetMapping("/{no}")
    public ResponseEntity<UserInfoResponseDto> userInfo(@PathVariable Long no) {

        return new ResponseEntity(new UserInfoResponseDto(), HttpStatus.OK);
    }
    
    // 회원 목록 조회
    @GetMapping
    public ResponseEntity<List<UserSimpleInfoDto>> userInfoList() {
        List<UserSimpleInfoDto> userSimpleInfoList = new ArrayList<>();
        
        return new ResponseEntity(userSimpleInfoList, HttpStatus.OK);
    }
    
    // 회원 정보 수정
    @PatchMapping("/{no}")
    public ResponseEntity<String> userUpdate(@PathVariable Long no, UserUpdateRequestDto userUpdateRequestDto) {
        return new ResponseEntity("수정 완료", HttpStatus.OK);
    }

    @DeleteMapping("/{no}")
    public ResponseEntity<String> userDelete(@PathVariable Long no) {
        return new ResponseEntity("삭제 완료", HttpStatus.OK);
    }

    @GetMapping("/block/{no}")
    public ResponseEntity<String> userBlock(@PathVariable Long no) {
        return new ResponseEntity("차단 완료", HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserSimpleInfoDto>> userSearch(UserSearchRequestDto userSearchRequestDto) {
        List<UserSimpleInfoDto> userSimpleInfoDtos = new ArrayList<>();
        return new ResponseEntity(userSimpleInfoDtos, HttpStatus.OK);
    }
}
