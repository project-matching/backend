package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

    @PostMapping
    @ApiOperation(value = "공지 알림 전송 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> notificationSend(String title, String content) {
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @GetMapping
    @ApiOperation(value = "알림 목록 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<List<NotificationSimpleInfoDto>>> notificationList() {
        List<NotificationSimpleInfoDto> notificationSimpleInfoDtoList = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/{notificationNo}")
    @ApiOperation(value = "알림 상세 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<NotificationDto>> notificationInfo(@PathVariable Long notificationNo) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }
}
