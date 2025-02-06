package com.learning.item.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.learning.item.model.Item;

@Repository
public class ItemRepository {
    private final Map<Integer, Item> items;
    private int currentId;
    private static final Logger logger = LoggerFactory.getLogger(ItemRepository.class);

    public ItemRepository() {
        items = new HashMap<Integer, Item>();
        String[] itemNames = new String[] {"Apple", "Banana", "Orange", "Bread", "Eggs", "Milk",
                "Juice", "Butter", "Cheese", "Tomato", "Cucumber", "Carrot", "Chicken", "Beef",
                "Fish", "Rice", "Pasta", "Yogurt", "Cereal", "Peanut Butter"};
        for (String itemName : itemNames) {
            Item item = new Item(currentId++, itemName, currentId + 0.99);
            items.put(item.getId(), item);
        }
    }
    
    private static void Wait() {
        String timeString = System.getenv("DEMO_WAIT_TIME");
        try {
            double time = Double.parseDouble(timeString);
            Thread.sleep((int)(time * 1000));
        } catch (Exception exception) {
            return;
        }
    }

    public Item findById(Integer id) {
        logger.info("Fetching item with ID {} from item repository. This might take a while...", id);
        Wait();
        return items.get(id).Copy();
    }

    public Collection<Item> getItems() {
        logger.info("Fetching all from item repository. This might take a while...");
        Wait();
        return items.values();
    }

    public void updateItem(Item item) {
        Item inMemoryItem = items.get(item.getId());
        inMemoryItem.setName(item.getName());
        inMemoryItem.setCost(item.getCost());
    }
}
