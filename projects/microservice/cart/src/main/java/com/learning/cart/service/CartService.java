package com.learning.cart.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.learning.cart.model.Cart;
import com.learning.cart.model.Item;

@Service
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CartService self;

    @Cacheable(value = "carts-cache", key = "#itemIds")
    public Cart getCart(List<Integer> itemIds) {
        double total = 0;
        Item[] items = new Item[itemIds.size()];
        logger.info("Calculating total for {} items. This might take a while...", items.length);
        for (int i = 0; i < items.length; i++) {
            Wait();
            items[i] = self.getItem(itemIds.get(i));
            total += items[i].getCost();
        }
        return new Cart(items, total);
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

    @Cacheable(value = "items-cache", key = "#id")
    public Item getItem(Integer id) {
        return restTemplate.getForObject("http://item/{id}", Item.class, id);
    }

    @SuppressWarnings("unchecked")
    public String getCacheStatsAsString() {
        StringBuilder builder = new StringBuilder();
        for (String name : cacheManager.getCacheNames()) {
            CacheStats stats = ((Cache<Object, Object>)cacheManager.getCache(name).getNativeCache()).stats();
            builder.append(String.format("%s: %s\n", name, stats));
        }
        return builder.toString();
    }

    @CacheEvict(value = {"items-cache", "carts-cache"}, allEntries = true)
    public void clearCache() {}
}
