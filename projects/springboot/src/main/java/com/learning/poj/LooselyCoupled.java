package com.learning.poj;

import com.learning.springboot.game.GameboyAdvance;
import com.learning.springboot.game.Mario;
import com.learning.springboot.game.Sonic;

/**
 * In this plain old java (POJ) example, games are loosely coupled dependancies.
 */
public class LooselyCoupled {
    public static void main(String[] args) {
        var gameboy = new GameboyAdvance();
        gameboy.setGame(new Mario());
        gameboy.play();
        gameboy.setGame(new Sonic());
        gameboy.play();
    }
}
