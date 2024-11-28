package com.example.final_jj.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class UserController {

    @GetMapping("/")
//    public String home() {
//        return "Welcome to Google OAuth Example!";
//    }
    public String home() {
        // 간단한 HTML 페이지를 반환하여 로그인 버튼을 제공
        return "<html>" +
                "<body>" +
                "<h1>Welcome to Google OAuth Example!</h1>" +
                "<a href=\"/oauth2/authorization/google\">Login with Google</a>" +
                "</body>" +
                "</html>";
    }

    @GetMapping("/user")
    public String user(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @GetMapping("/hi")
    public String hi() {
        log.error("message {}", "재형바보");
        return "hihi";
    }
}
