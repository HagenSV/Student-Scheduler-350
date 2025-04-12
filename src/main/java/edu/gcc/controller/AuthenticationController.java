package edu.gcc.controller;

import edu.gcc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/login")
//    public String login(@RequestBody LoginRequest loginRequest) {
//        return "login";
//    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest loginRequest) {
        if (!loginRequest.email().equals(loginRequest.email2())) {

            return "Email addresses do not match";
        }
        return "Success";
    }

    public record RegisterRequest(String email, String email2, String password) {}
    public record LoginRequest(String email, String password) {}

}
