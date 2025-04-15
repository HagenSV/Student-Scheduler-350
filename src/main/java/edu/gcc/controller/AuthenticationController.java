package edu.gcc.controller;

import edu.gcc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
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
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword) {
        System.out.println("Processing request");
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "redirect:/register?error";
        }
        if (userService.getUserByUsername(username) != null){
            return "redirect:/register?error";
        }
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error";
        }
        userService.registerUser(username, password);
        return "redirect:/login";
    }

}
