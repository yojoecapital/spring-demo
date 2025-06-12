package com.learning.sprintsecurity.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BooksController {
    @GetMapping("/books")
    public String getBooksForBusiness(@RequestParam String business) {
        return "Get books for business: " + business;
    }

    @DeleteMapping("/books")
    public String deleteBooksForBusiness(@RequestParam String business) {
        return "Delete books for business: " + business;
    }

    @PostMapping("/books")
    public String createBooksForBusiness(@RequestParam String business) {
        return "Post books for business: " + business;
    }
}
