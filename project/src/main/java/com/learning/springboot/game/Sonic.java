package com.learning.springboot.game;

import org.springframework.stereotype.Component;

@Component
public class Sonic implements Game {

    @Override
    public void jump() {
        System.out.println("Sonic is jumping!");
    }

    @Override
    public String toString() {
        return "Sonic the Hedgehog 1991";
    }
    
}
