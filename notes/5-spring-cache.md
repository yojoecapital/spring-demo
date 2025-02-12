# 5. Spring Boot cache

> These notes will use the code in the `microservice` project.

## Materials

- https://www.baeldung.com/java-caching-caffeine
- https://www.baeldung.com/spring-boot-caffeine-cache

## Caffeine

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>*.*.*</version>
</dependency>
```

Inside of the `ItemService`, we can create an instance of a `Caffeine` cache.

```java
private final Cache<Integer, Item> cache;
public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
    cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES) // items go stale after 1 minute
            .maximumSize(5) // max number of entires before eviction
            // listeners
            .removalListener(ItemService::LogCacheRemovalCause)
            .evictionListener(ItemService::LogCacheEvictionCause)
        	// include this so the cache.stats() method returns something usefull
        	.recordStats();
            .build()
}
```

### Eviction versus Removal

- **eviction** happens automatically based on the cache configuration
  - `expireAfterWrite`
  - `maximumSize`
- **removal** happens explicitly and is usually driven by the application logic
  - calling `cache.invalidateAll()`

### Expire versus refresh

- `expireAfterWrite` or `expireAfterAccess`
  - when an entry *expires*, it gets completely *removed* from the cache
  - if a request comes in for an expired entry, the request is blocked until a new value is computed and stored
  - this means a user requesting expired data will experience a delay as the new value is generated
- `refreshAfterWrite`
  - when an item is *eligible for a refresh*, the cache still returns the old value *immediately*
  - the cache then asynchronously fetches the new value in the background
  - in order to use `refreshAfterWrite`, you'll need to supply a loading function to `build`
  - when `build` has a loading function, it returns a `LoadingCache`

```java
cache = Caffeine.newBuilder()
    .refreshAfterWrite(1, TimeUnit.MINUTES)
    .maximumSize(5)
    .build(itemRepository::findById);
```

### Getting data from the cache

- using `cache.getIfPresent(key)` will return `null` if the value is not present
- using `cache.get(key)` will behave similarly to `getIfPresent` 
  - unless it is a `LoadingCache`
  - instead, it will return the entry by running the load function that was used to build it
- you can also explicitly pass a load function to `cache.get(key, mappingFunction)` as well
- load functions are ran atomically meaning they are only run once per computation before eviction
  - for example, say 2 threads request key `1` and `1` is not in the cache
  - then the computation is only ran once 
  - *both* threads are blocked until the computation completes
  - for this reason, using a load function is preferable than checking `getIfPresent` and then using `put` in the case that it returns `null`
- you can also use `getAll` and pass an array of keys to get multiple results back

### Asynchronous cache

By using `buildAsync`, you can build a `AsyncLoadingCache` where the get methods will return a `CompletableFuture` which is similar to a promise or `Mono`.

## Using Spring Boot and Caffeine cache

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

To enable Spring's annotation-driven cache management, you can use the `@EnableCaching`. This will automatically set up a `CacheManager` bean.

```java
@SpringBootApplication
@EnableCaching
public class Application
```

- Spring will try to automatically detect the caching provider from the classpath (I think)
- otherwise, by default, the `CacheManager` bean will just use a simple `ConcurrentMapCacheManager` (which does not have eviction or expiration or cool-dude features)
- you can also manually override the `CacheManager` bean and specify what implementation to use by instead putting the `@EnableCaching` on a configuration class and defining a `CacheManager` bean

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(5);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
```

- in the code block above, I'm passing a `Caffeine` cache builder (not the cache itself) with it's configured properties
- any caches that are referenced but do not exist yet in annotations like `@Cacheable` will be created using this default cache builder
- you can also manually add in a custom cache with it's own properties specified apart from the default cache builder by using `registerCustomCache` as such

```java
Cache<Object, Object> itemsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(5);
CaffeineCacheManager cacheManager = new CaffeineCacheManager();
cacheManager.registerCustomCache("items-cache", itemsCache);
```

### Annotations

```java
@Cacheable(value = "items-cache", key = "#id")
public Item getItem(Integer id) {
    return restTemplate.getForObject("http://item/{id}", Item.class, id);
}
```

- in my service method, I can add the annotation `@Cacheable` to tell spring to first check the cache before actually running the method
  - it's a cache hit, the method is never ran
  - if it's a cache miss, then the method runs and the result is cached
- `value = "items-cache"` specifies what the cache name is
  - for example, I can have a different cache for a different object type that is built with the same `Caffeince` builder
-  `key = "#id"` is specified in "Spring Expression Language" or [SpEL](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html)
  - the `#` symbol refers to method itself
  - `#id` is equivalent to `#root.args[0]` which points to the first argument of the method

```java
@CachePut(value = "items-cache", key = "#id")
```

- `@CachePut` is similar to `@Cacheable` but it doesn't cause the method to be skipped over in the case of a cache hit

```java
@CacheEvict(value = {"carts-cache", "items-cache"}, allEntries = true)
public void clearCache() {}
```

- this method will evict all entires in `"carts-cache"` and `"items-cache"` because of `allEntries`
- a `key` can also be given to evict by key

#### Self-invocation

- it is important to note that Spring’s `@Cacheable` annotation will only work when a bean calls the method from another bean
- not when it's called internally within the same class
- if self-invocation in a class is required for caching, you'll have to inject a bean of it's own type 
- here's an example of that:

```java
@Autowired
private CartService self; // self injected bean

@Cacheable(value = "carts-cache", key = "#itemIds")
public Cart getCart(List<Integer> itemIds) {
    double total = 0;
    Item[] items = new Item[itemIds.size()];
    for (int i = 0; i < items.length; i++) {
        // call getItem from the context of a bean
        items[i] = self.getItem(itemIds.get(i));
        total += items[i].getCost();
    }
    return new Cart(items, total);
}

@Cacheable(value = "items-cache", key = "#id")
public Item getItem(Integer id) {
    return restTemplate.getForObject("http://item/{id}", Item.class, id);
}
```

## Running demo

```bash
cd ~/projects/microservice
sdk use java 8.0.422-amzn 

# set DEMO_WAIT_TIME to give mock processes a wait time in seconds
DEMO_WAIT_TIME=1

# starting and stopping
./run.sh
./stop.sh
```

### Endpoints

- GET `http://localhost:8084/item/cache`: get item cache stats
- GET `http://localhost:8084/cart/cache`: get cart cache stats
- DELETE `http://localhost:8084/item/cache`: clear item cache
- DELETE `http://localhost:8084/cart/cache`: clear cart cache
