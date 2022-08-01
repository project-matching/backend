package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.user.*;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import com.matching.project.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;

    @ApiOperation(value = "유저 정보")
    @GetMapping("/info")
    public ResponseEntity<ResponseDto<UserInfoResponseDto>> userInfo() {
        return ResponseEntity.ok().body(new ResponseDto<>(null, userService.getUserInfo()));
    }

    @PostMapping
    @ApiOperation(value = "회원가입")
    public ResponseEntity<ResponseDto<Boolean>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        User user = userService.userSignUp(signUpRequestDto);

        // Valid Authentication Token Save
        EmailAuth emailAuth = emailService.emailAuthTokenSave(signUpRequestDto.getEmail(), EmailAuthPurpose.EMAIL_AUTHENTICATION);

        // Send Email
        emailService.sendConfirmEmail(user.getEmail(), emailAuth.getAuthToken());

        return ResponseEntity.ok().body(new ResponseDto<Boolean>(null, true));
    }

    @ApiOperation(value = "이메일 인증")
    @GetMapping("/confirm")
    public ResponseEntity<ResponseDto<String>> confirmEmail(@Valid EmailAuthRequestDto dto) {
        User user = emailService.checkConfirmEmail(dto, EmailAuthPurpose.EMAIL_AUTHENTICATION);
        TokenDto tokenDto = TokenDto.builder()
                .email(user.getEmail())
                .build();
        String jwtAccessToken = jwtTokenService.createToken(tokenDto);
        return ResponseEntity.ok().body(new ResponseDto<>(null, jwtAccessToken));
    }

    @ApiOperation(value = "이메일 재발송 (회원가입)")
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<Boolean>> reSendEmailAuth(@RequestBody @Valid EmailAuthReSendRequestDto dto) {
        // Before Send Working
        EmailAuth emailAuth = emailService.beforeSendWork(dto.getEmail(), EmailAuthPurpose.EMAIL_AUTHENTICATION);

        // Send Email
        emailService.sendConfirmEmail(dto.getEmail(), emailAuth.getAuthToken());

        return ResponseEntity.ok().body(new ResponseDto<>(null, true));
    }

    @GetMapping
    @ApiOperation(value = "내 프로필 조회")
    public ResponseEntity<ResponseDto<UserProfileInfoResponseDto>> userProfile() {
        UserProfileInfoResponseDto userProfileInfoResponseDto = userService.userProfileInfo();
        return ResponseEntity.ok(new ResponseDto<>(null, userProfileInfoResponseDto));
    }

    @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "개인정보 변경")
    public ResponseEntity<ResponseDto<Boolean>> userProfileUpdate(
            @RequestPart("data") @Valid UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart("image") MultipartFile file) {
        User user = userService.userUpdate(userUpdateRequestDto, file);
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @PatchMapping("/password")
    @ApiOperation(value = "비밀번호 변경")
    public ResponseEntity<ResponseDto<Boolean>> userPasswordUpdate(@RequestBody @Valid PasswordUpdateRequestDto passwordUpdateRequestDto) {
        User user = userService.userPasswordUpdate(passwordUpdateRequestDto);
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }


    @DeleteMapping
    @ApiOperation(value = "회원 탈퇴")
    public ResponseEntity<ResponseDto<Boolean>> userSingOut() {
        User user = userService.userSignOut();
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @GetMapping("/list")
    @ApiOperation(value = "회원 목록 조회 (관리자)")
    public ResponseEntity<ResponseDto<List<UserSimpleInfoDto>>> userInfoList(@Valid UserFilterDto userFilterDto,
                                                                             @PageableDefault(size = 5) Pageable pageable) {
        List<UserSimpleInfoDto> dtoList = userService.userInfoList(userFilterDto, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, dtoList));
    }
    @GetMapping("/block/{userNo}")
    @ApiOperation(value = "회원 차단 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> userBlock(@PathVariable Long userNo, @RequestBody String reason) {
        User user = userService.userBlock(userNo, reason);
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @GetMapping("/unblock/{userNo}")
    @ApiOperation(value = "회원 차단 해제 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> userUnBlock(@PathVariable Long userNo) {
        User user = userService.userUnBlock(userNo);
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }
}
