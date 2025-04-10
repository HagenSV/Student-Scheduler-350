package edu.gcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {

    @GetMapping({"/", "/search","/{path:^(?!api|static)}"})
    public String serveReactApp() {
        return "forward:/index.html";
    }
}
