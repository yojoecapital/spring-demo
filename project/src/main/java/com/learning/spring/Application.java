package com.learning.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.learning.springboot.game.Gameboy;
import com.learning.springboot.game.GameboyAdvance;

/**
 * This is an example of Spring managing DI using am XML configuration.
 */
public class Application {
    public static void main(String[] args) {
        try (var context = new ClassPathXmlApplicationContext("spring.xml")) {
            var gameboyAdvance = context.getBean(GameboyAdvance.class);
            gameboyAdvance.play();
            var gameboy = context.getBean(Gameboy.class);
            gameboy.play();
        }
    }
}
