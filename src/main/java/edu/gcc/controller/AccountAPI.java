package edu.gcc.controller;

import edu.gcc.AuthenticatedUserUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountAPI {


    @GetMapping("/api/v1/name")
    public String getName() {
        // This method will handle GET requests to the /api/v1/name endpoint
        // You can implement the logic to retrieve and return the name here
        return AuthenticatedUserUtil.getAuthenticatedUser(); // Replace with actual logic to get the name
    }
}
