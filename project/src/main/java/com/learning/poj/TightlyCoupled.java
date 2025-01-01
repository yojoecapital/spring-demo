package com.learning.poj;

import com.learning.springboot.game.Gameboy;
import com.learning.springboot.game.Mario;

/**
 * In this plain old java (POJ) example, Mario is a tightly coupled dependancy.
 */
public class TightlyCoupled {
    public static void main(String[] args) {
        var gameboy = new Gameboy(new Mario());
        gameboy.play();
    }
}
