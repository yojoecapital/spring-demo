package com.learning.mvc.controller;

import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping
    public String index() {
        return "forward:/home.html";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("message", "This is being rendered with Thymeleaf!");
        model.addAttribute("time", LocalDateTime.now());
        return "about";
    }
}
