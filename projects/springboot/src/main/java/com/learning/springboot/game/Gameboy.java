package com.learning.springboot.game;

import org.springframework.stereotype.Component;

/**
 * A demo for constructor injection.
 */
@Component
public class Gameboy {
    Mario game;

    public Gameboy(Mario game) {
        this.game = game;
    }

    public void play() {
        System.out.println("Gameboy is now playing: \"" + game + "\"");
        game.jump();
    }
}
