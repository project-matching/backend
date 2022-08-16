package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/test")
    public ResponseEntity<ResponseDto<String>> test(@RequestBody testDto testDto) {
        return ResponseEntity.ok(new ResponseDto<String>(null,"aa"));
    }

    @GetMapping("/test2")
    public ResponseEntity<ResponseDto<String>> test2(String a) {
        return ResponseEntity.ok(new ResponseDto<String>(null,"aa"));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class testDto {
    List<String> test1;
    Integer test2;
}
