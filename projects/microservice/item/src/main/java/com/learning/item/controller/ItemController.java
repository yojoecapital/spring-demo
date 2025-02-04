package com.learning.item.controller;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.learning.item.model.Item;
import com.learning.item.repository.ItemRepository;

@RestController
public class ItemController {
    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Collection<Item> getItems() {
        return itemRepository.getItems();
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable("id") int id) {
        return itemRepository.findById(id);
    }
}
