package com.boulisa.dms.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/api/v1/auth/signup")
    public String signup() { return "signup-endpoint"; }

    @GetMapping("/api/v1/auth/login")
    public String login() {
        return "login-endpoint";
    }

}
