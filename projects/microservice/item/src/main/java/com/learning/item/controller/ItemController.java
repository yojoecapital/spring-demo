package com.learning.item.controller;

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

    @GetMapping("/{id}")
    public Item getItem(@PathVariable("id") int id) {
        return itemRepository.findById(id);
    }

    @GetMapping("/{id}/{count}")
    public Item[] getItems(@PathVariable("id") int id, @PathVariable int count) {
        Item item = itemRepository.findById(id);
        Item[] items = new Item[count];
        for (int i = 0; i < count; i++)
            items[i] = item;
        return items;
    }
}
