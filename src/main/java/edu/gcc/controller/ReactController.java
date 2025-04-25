package edu.gcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {

    @GetMapping({"/","/login","/register","/search"})
    public String serveReactApp() {
        //System.out.println("Serving React app");
        return "forward:/index.html";
    }
}
