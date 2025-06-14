package com.learning.sprintsecurity.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MoviesController {
    @GetMapping("/movies")
    public String getMoviesForBusiness(@RequestParam String business) {
        return "Get movies for business: " + business;
    }

    @DeleteMapping("/movies")
    public String deleteMoviesForBusiness(@RequestParam String business) {
        return "Delete movies for business: " + business;
    }

    @PostMapping("/movies")
    public String createMoviesForBusiness(@RequestParam String business) {
        return "Post movies for business: " + business;
    }
}
