package com.learning.cart.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.learning.cart.model.Cart;
import com.learning.cart.service.CartService;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;
    
    @GetMapping("/cache")
    public String getCacheStats() {
        return String.join("\n",
                cartService.getCacheStats().map(cache -> cache.toString()).toArray(String[]::new));
    }
    
    @DeleteMapping("/cache")
    public void clearCache() {
        cartService.clearCache();
    }

    @GetMapping
    public Cart getCart(@RequestBody List<Integer> itemIds) {
        return cartService.getCart(itemIds);
    }
}
