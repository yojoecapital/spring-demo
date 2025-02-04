package com.learning.item.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.learning.item.model.Item;

@Repository
public class ItemRepository {
    private final Map<Integer, Item> items;
    private int currentId;

    public ItemRepository() {
        items = new HashMap<Integer, Item>();
        String[] itemNames = new String[] { "Apple", "Banana", "Orange", "Bread", "Eggs", "Milk", "Juice" };
        for (String itemName : itemNames) {
            Item item = new Item(currentId++, itemName, currentId + 0.99);
            items.put(item.getId(), item);
        }
    }

    public Item findById(Integer id) {
        return items.get(id);
    }

    public Collection<Item> getItems() {
        return items.values();
    }
}
