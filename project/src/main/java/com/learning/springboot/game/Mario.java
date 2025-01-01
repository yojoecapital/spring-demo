package com.learning.springboot.game;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class Mario implements Game {
    public void jump() {
        System.out.println("Mario is jumping!");
    }

    @Override
    public String toString() {
        return "Super Mario Bros. 1985";
    }
}
