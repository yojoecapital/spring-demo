package com.learning.cart.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.learning.cart.model.Item;

@RestController
public class CartController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Item[] getCart(@RequestBody List<Integer> itemIds) {
        return itemIds.stream().map(this::getItem).toArray(Item[]::new);
    }

    private Item getItem(Integer id) {
        return restTemplate.getForObject("http://item/{id}", Item.class, id);
    }
}
