package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.SliceDto;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.token.TokenDto;
import com.matching.project.dto.user.*;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import com.matching.project.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
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
    @ApiOperation(value = "회원 가입")
    public ResponseEntity<ResponseDto<Boolean>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        User user = userService.userSignUp(signUpRequestDto);

        // Valid Authentication Token Save
        EmailAuth emailAuth = emailService.emailAuthTokenSave(signUpRequestDto.getEmail(), EmailAuthPurpose.EMAIL_AUTHENTICATION);

        // Send Email
        emailService.sendConfirmEmail(user.getEmail(), emailAuth.getAuthToken());

        return ResponseEntity.ok().body(new ResponseDto<Boolean>(null, true));
    }

    @ApiOperation(value = "이메일 인증")
    @PostMapping("/confirm")
    public ResponseEntity<ResponseDto<TokenDto>> confirmEmail(@RequestBody @Valid EmailAuthRequestDto dto) {
        User user = emailService.checkConfirmEmail(dto, EmailAuthPurpose.EMAIL_AUTHENTICATION);
        TokenClaimsDto tokenClaimsDto = TokenClaimsDto.builder()
                .email(user.getEmail())
                .build();
        return ResponseEntity.ok().body(new ResponseDto<>(null, jwtTokenService.createToken(tokenClaimsDto)));
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
    @ApiOperation(value = "내 프로필 조회"
                    ,notes = "loginCategory => [NORMAL, GITHUB, GOOGLE]")
    public ResponseEntity<ResponseDto<UserProfileInfoResponseDto>> userProfile() {
        UserProfileInfoResponseDto userProfileInfoResponseDto = userService.userProfileInfo();
        return ResponseEntity.ok(new ResponseDto<>(null, userProfileInfoResponseDto));
    }

    @PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "개인정보 변경", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Boolean>> userProfileUpdate(@Valid UserUpdateRequestDto userUpdateRequestDto,
                                                                  @RequestPart(value = "image", required = false) MultipartFile file) {
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
    public ResponseEntity<ResponseDto<SliceDto<UserSimpleInfoDto>> >userInfoList(Long userNo,
                                                              @Valid UserFilterDto userFilterDto,
                                                              @PageableDefault(size = 5) Pageable pageable) {
        SliceDto<UserSimpleInfoDto> dtoList = userService.userInfoList(userNo, userFilterDto, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, dtoList));
    }

    @PatchMapping("/block/{userNo}")
    @ApiOperation(value = "회원 차단 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> userBlock(@PathVariable Long userNo, @RequestBody UserBlockRequestDto userBlockRequestDto) {
        User user = userService.userBlock(userNo, userBlockRequestDto.getBlockReason());
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @PatchMapping("/unblock/{userNo}")
    @ApiOperation(value = "회원 차단 해제 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> userUnBlock(@PathVariable Long userNo) {
        User user = userService.userUnBlock(userNo);
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }
}
