package com.matching.project.controller;

import com.matching.project.dto.notification.NotificationDto;
import com.matching.project.dto.notification.NotificationSimpleInfoDto;
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
    public ResponseEntity notificationSend(NotificationDto notificationSendRequestDto) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity notificationList() {
        List<NotificationSimpleInfoDto> notificationSimpleInfoDtoList = new ArrayList<>();
        return new ResponseEntity(notificationSimpleInfoDtoList, HttpStatus.OK);
    }

    @GetMapping("/{notificationNo}")
    public ResponseEntity notificationInfo(@PathVariable Long notificationNo) {
        return new ResponseEntity(new NotificationDto(), HttpStatus.OK);
    }
}
