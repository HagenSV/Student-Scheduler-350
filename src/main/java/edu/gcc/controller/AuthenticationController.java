package edu.gcc.controller;

import edu.gcc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String register(@RequestParam("email") String email,
                           @RequestParam("confirm") String confirm,
                           @RequestParam("password") String password) {
        System.out.println("Processing request");
        if (!email.equals(confirm)) {
            return "redirect:/register?error";
        }
        //TODO: fix database connection
        //userService.registerUser(email, password);
        return "redirect:/login";
    }

}
