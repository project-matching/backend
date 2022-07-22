package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.user.*;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.service.EmailService;
import com.matching.project.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @ApiOperation(value = "유저 정보 (수정 완료)")
    @GetMapping("/info")
    public ResponseEntity<ResponseDto<UserSimpleInfoDto>> getUserInfo() {
//        try {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            if (!(auth instanceof AnonymousAuthenticationToken)) {
//                User user = (User)auth.getPrincipal();
//                            UserSimpleInfoDto dto = UserSimpleInfoDto.builder()
//                    .no(user.getNo())
//                    .name(user.getName())
//                    //.profile(null)
//                    .build();
//                ResponseDto<UserSimpleInfoDto> response = ResponseDto.<UserSimpleInfoDto>builder().data(dto).build();
//                return ResponseEntity.ok().body(response);
//            }
//            else {
//                ResponseDto<String> response = ResponseDto.<String>builder().data(auth.getPrincipal().toString()).build();
//                return ResponseEntity.ok().body(response);
//            }
//        } catch (Exception e) {
//            ResponseDto<String> response = ResponseDto.<String>builder().error(null).build();
//            return ResponseEntity.badRequest().body(response);
//        }
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PostMapping
    @ApiOperation(value = "회원가입 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        try {
            User user = userService.userSignUp(signUpRequestDto);
            // 클라이언트에서 dto 정보가 추가적으로 더 필요하면 수정 필요

            // Valid Authentication Token Save
            EmailAuth emailAuth = emailService.emailAuthTokenSave(signUpRequestDto.getEmail(), EmailAuthPurpose.EMAIL_AUTHENTICATION);

            // Send Email
            emailService.sendConfirmEmail(user.getEmail(), emailAuth.getAuthToken());
            return ResponseEntity.ok().body(new ResponseDto<Boolean>(null, true));
        } catch (Exception e) {
            ResponseDto responseDto = ResponseDto.builder()
                    .error(null).build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @ApiOperation(value = "이메일 인증 (수정 필요)")
    @GetMapping("/confirm")
    public ResponseEntity<ResponseDto<String>> confirmEmail(EmailAuthRequestDto dto) {
        try {
            dto.setPurpose(EmailAuthPurpose.EMAIL_AUTHENTICATION);
            emailService.CheckConfirmEmail(dto);
            ResponseDto responseDto = ResponseDto.builder()
                    //TODO
                    // JWT 반환
                    .data("JWT").build();
            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            ResponseDto responseDto = ResponseDto.builder()
                    .error(null).build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @ApiOperation(value = "이메일 재발송 (회원가입) (수정 필요)")
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<String>> reSendEmailAuth(@RequestBody EmailAuthReSendRequestDto dto) {
        try {
            dto.setPurpose(EmailAuthPurpose.EMAIL_AUTHENTICATION);

            EmailAuth emailAuth = emailService.beforeSendWork(dto.getEmail(), dto.getPurpose());
            emailService.sendConfirmEmail(dto.getEmail(), emailAuth.getAuthToken());

            ResponseDto responseDto = ResponseDto.builder()
                    // TODO
                    // JWT로 바꿔야함
                    .data("JWT").build();
            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            ResponseDto responseDto = ResponseDto.builder()
                    .error(null).build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }
    
//    @GetMapping("/{no}")
//    @ApiOperation(value = "회원 정보 조회")
//    public ResponseEntity userInfo(@PathVariable Long no) {
//        try {
//            UserInfoResponseDto userInfoResponseDto = userService.userInfo(no);
//
//            ResponseDto responseDto = ResponseDto.builder()
//                    .data(userInfoResponseDto).build();
//            return ResponseEntity.ok().body(responseDto);
//        } catch (Exception e) {
//            ResponseDto responseDto = ResponseDto.builder()
//                    .error(null).build();
//            return ResponseEntity.badRequest().body(responseDto);
//        }
//    }

    @GetMapping("/list")
    @ApiOperation(value = "(관리자)회원 목록 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<Page<UserSimpleInfoDto>>> userInfoList(@PageableDefault(size = 5) Pageable pageable, UserFilterDto userFilterDto) {
        List<UserSimpleInfoDto> dtoList = userService.userInfoList(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .data(dtoList).build();
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

//    @PatchMapping("/{no}")
//    @ApiOperation(value = "회원 정보 수정")
//    public ResponseEntity userUpdate(@PathVariable Long no, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
//        try {
//            User user = userService.userUpdate(no, userUpdateRequestDto);
//            ResponseDto responseDto = ResponseDto.builder()
//                    .data(user.getNo()).build();
//            return ResponseEntity.ok().body(responseDto);
//        } catch (Exception e) {
//            ResponseDto responseDto = ResponseDto.builder()
//                    .error(null).build();
//            return ResponseEntity.badRequest().body(responseDto);
//        }
//    }

//    @DeleteMapping("/{no}")
//    @ApiOperation(value = "회원 탈퇴")
//    public ResponseEntity userDelete(@PathVariable Long no, @RequestBody SignOutRequestDto signOutRequestDto) {
//        try {
//            Long deleteNo = userService.userSignOut(no, signOutRequestDto);
//            ResponseDto responseDto = ResponseDto.builder()
//                    .data(deleteNo).build();
//            return ResponseEntity.ok().body(responseDto);
//        } catch (Exception e) {
//            ResponseDto responseDto = ResponseDto.builder()
//                    .error(null).build();
//            return ResponseEntity.badRequest().body(responseDto);
//        }
//    }

    @GetMapping("/block/{userNo}")
    @ApiOperation(value = "(관리자) 회원 차단 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> userBlock(@PathVariable Long userNo, @RequestBody String reason) {
        // User user = userService.userBlock(no, userBlockRequestDto);
//        UserBlockResponseDto userBlockResponseDto = UserBlockResponseDto.builder()
//                .email(user.getEmail())
//                .block(user.isBlock())
//                .blockReason(user.getBlockReason())
//                .build();
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/unblock/{userNo}")
    @ApiOperation(value = "(관리자) 회원 차단 해제 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> userUnBlock(@PathVariable Long userNo) {
        User user = userService.userUnBlock(userNo);
        UserBlockResponseDto userBlockResponseDto = UserBlockResponseDto.builder()
                .email(user.getEmail())
                .block(user.isBlock())
                .build();
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping
    @ApiOperation(value = "내 프로필 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<UserProfileDto>> userProfile() {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PatchMapping
    @ApiOperation(value = "개인정보 변경 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> userProfileUpdate(UserUpdateRequestDto userUpdateRequestDto) {
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @PatchMapping("/password")
    @ApiOperation(value = "비밀번호 변경 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> userPasswordUpdate(String oldPassword, String newPassword) {
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @DeleteMapping
    @ApiOperation(value = "회원 탈퇴 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> userDelete() {
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }
}
