package edu.gcc.controller;

import edu.gcc.DbConnection;
import edu.gcc.UpdateDatabaseContents;
import edu.gcc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;

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
            return "redirect:/register?error=empty";
        }

        if (userService.getUserByUsername(username) != null){
            return "redirect:/register?error=taken";
        }

        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=mismatch";
        }

        userService.registerUser(username, password);

        // Initialize user schedules
        //Connection db = DbConnection.getConnection();
        UpdateDatabaseContents.createSchedules(username);


        return "redirect:/login";
    }

}
