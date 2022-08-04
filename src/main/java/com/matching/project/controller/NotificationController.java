package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSendRequestDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
import com.matching.project.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ApiOperation(value = "공지 알림 전송 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> notificationSend(@RequestBody @Valid NotificationSendRequestDto notificationSendRequestDto) {
        notificationService.sendNotification(Type.NOTICE, null, notificationSendRequestDto.getTitle(), notificationSendRequestDto.getContent());
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @GetMapping
    @ApiOperation(value = "알림 목록 조회")
    public ResponseEntity<ResponseDto<List<NotificationSimpleInfoDto>>> notificationList(@PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(new ResponseDto<>(null, notificationService.notificationList(pageable)));
    }

    @GetMapping("/{notificationNo}")
    @ApiOperation(value = "알림 상세 조회")
    public ResponseEntity<ResponseDto<NotificationDto>> notificationInfo(@PathVariable Long notificationNo) {
        return ResponseEntity.ok(new ResponseDto<>(null, notificationService.notificationInfo(notificationNo)));
    }
}