package com.learning.cart.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.learning.cart.model.Item;

@RestController
public class CartController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<Item> getCart() {
        List<Item> list = new ArrayList<Item>();
        for (int i = 0; i < 4; i++) {
            Item item = restTemplate.getForObject("http://item/{id}", Item.class, i);
            list.add(item);
        }
        return list;
    }
}
