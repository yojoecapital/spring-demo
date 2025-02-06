package com.learning.item.controller;

import java.util.Collection;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.learning.item.model.Item;
import com.learning.item.repository.ItemRepository;
import com.learning.item.service.ItemService;

@RestController
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public ItemController(ItemService itemService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/cache")
    public String getCacheStats() {
        return itemService.getCacheStats().toString();
    }

    @DeleteMapping("/cache")
    public void clearCache() {
        itemService.clearCache();
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable("id") Integer id) {
        return itemService.findById(id);
    }

    @PutMapping
    public Item updateItem(@RequestBody Item item) {
        itemService.updateItem(item);
        return item;
    }

    @GetMapping
    public Collection<Item> getItems() {
        return itemRepository.getItems();
    }

}
