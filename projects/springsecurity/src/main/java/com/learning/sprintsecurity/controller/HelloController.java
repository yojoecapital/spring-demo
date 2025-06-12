package com.learning.sprintsecurity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HelloController {
    @GetMapping("/hello")
    public String getBooksForBusiness(Authentication authentication) {
        return "Hello, you're accessing this endpoint from " + authentication.getName() + "!";
    }
}
