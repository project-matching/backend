package com.matching.project.controller;

import com.matching.project.oauth.UserSessionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        UserSessionDto user = (UserSessionDto) httpSession.getAttribute("user");

        if (user != null) {
            log.info(user.getName());
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }
}
