package com.learning.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is an example of a Spring Boot MVC web application.
 * Note that the @SpringBootApplication annotation is equivalent to the below
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
