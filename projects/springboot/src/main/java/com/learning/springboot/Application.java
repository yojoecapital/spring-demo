package com.learning.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.learning.springboot.game.Gameboy;
import com.learning.springboot.game.GameboyAdvance;

/**
 * This is an example of Spring managing DI using Spring Boot's annotations.
 * Note that the @SpringBootApplication annotation is a shortcut for what is below.
 */
@Configuration  // Marks the class as a configuration class (bean definitions)
@EnableAutoConfiguration  // Enables auto-configuration of Spring Boot
@ComponentScan(basePackages = "com.learning.springboot")  // Scans the specified package for components
public class Application {
    public static void main(String[] args) {
        var context = SpringApplication.run(Application.class, args);
        var gameboyAdvance = context.getBean(GameboyAdvance.class);
        gameboyAdvance.play();
        var gameboy = context.getBean(Gameboy.class);
        gameboy.play();
    }
}
