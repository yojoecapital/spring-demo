package com.learning.springboot.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * A demo for setter injection.
 */
@Component
public class GameboyAdvance {
    Game game;

    public GameboyAdvance(Game game) {
        System.out.println("I'm alive!");
        this.game = game;
    }

    public GameboyAdvance() {}

    @Autowired
    @Qualifier("sonic")
    /*
     * The @Qualifier annotation resolves ambiguity on which Game should be injected.
     * Note that the annotation could also be placed inline with the parameter like `(@Qualifier("sonic") Game game)`
     * By default, the name of the component is its own identifier but with a lowercased first letter ("sonic" for Sonic).
     * Another way to resolve ambiguity is by using the @Primary annotation on the injected component (refer to Gameboy).
     */
    public void setGame(Game game) {
        this.game = game;
    }

    public void play() {
        System.out.println("Gameboy Advance is now playing: \"" + game + "\"");
        game.jump();
    }
}
