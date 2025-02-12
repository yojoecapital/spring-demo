package com.learning.item.service;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.learning.item.model.Item;
import com.learning.item.repository.ItemRepository;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;
    private final LoadingCache<Integer, Item> cache;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        cache = Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(5)
                .removalListener(ItemService::LogCacheRemovalCause)
                .evictionListener(ItemService::LogCacheEvictionCause)
                .recordStats()
                .build(itemRepository::findById);
    }
    
    private static void LogCacheRemovalCause(Integer key, Item value, RemovalCause removalCause) {
        logger.info("Removed ({}, {}). Reason: {}.", key, value, removalCause);
    }

    private static void LogCacheEvictionCause(Integer key, Item value, RemovalCause removalCause) {
        logger.info("Evicted ({}, {}). Reason: {}.", key, value, removalCause);
    }

    public Item findById(Integer id) {
        return cache.get(id);
    }

    public void updateItem(Item item) {
        logger.info("Invalidating cache on ID {}...", item.getId());
        cache.invalidate(item.getId());
        itemRepository.updateItem(item);
    }

    public CacheStats getCacheStats() {
        return cache.stats();
    }

    public void clearCache() {
        cache.invalidateAll();
    }
}
