# Spring Boot micro-services

In this demo, I'll using Java 8, Eureka, and Zuul to create an application with micro-service architecture.

> These notes will use the code in the `microservice` project.

## What is Eureka?

- Eureka is a service discovery tool developed by Netflix
- it helps microservices register *themselves* and discover other services dynamically

### Eureka Discovery Server

To start a Eureka discovery server, you'll need the `@EnableEurekaServer` annotation in your spring boot project.

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}

```

To configure how the Eureka server behaves, you can add these options to `src/main/resources/application.yml` (or `application.properites`).

```yml
server:
  port: 8761 # this is the default Eureka server port but I have it hard coded

eureka:
  client:
    registerWithEureka: false  # Don't register this server with Eureka
    fetchRegistry: false # Don't try to fetch from Eureka
```

Notice that a Eureka server can *discover* itself such that you can have multiple discovery servers working together. In this case, we'll just have 1 discovery server and turn this behavior off.

#### Eureka dashboard

When the Eureka discovery server is up, you can access a developer dashboard at `http://localhost:8761/` to see what clients registered with the discovery service.

### Eureka Clients

To register a client application with the discovery server, you can use the `@EnableEurekaClient` annotation.

```java
@SpringBootApplication
@EnableEurekaClient
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
```

To configure how the client behaves, again look at `application.properites`.

```properties
spring.application.name=cart
server.port=8082
# this is the default zone that the client will query but I have it hard coded
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
```

#### How can a client get in touch with another client?

- Spring will automatically resolve services based on their application name (or service ID) when `RestTemplate` or `WebClient`
  - `RestTemplate` uses a synchronous approach for HTTP requests 
  - `WebClient` uses an asynchronous approach that you can modify with a "builder" pattern
- in this code snippet, the `cart` service makes a call to the `item` service to get an item's information by its item ID
  - notice that `"http://item/..."` is not an actual URL
  - Spring will resolve this pattern to find the right service registered with Eureka (which has the service ID of `item`)

```java
@GetMapping
public Item[] getCart(@RequestBody List<Integer> itemIds) {
    return itemIds.stream().map(this::getItem).toArray(Item[]::new);
}

private Item getItem(Integer id) {
    return restTemplate.getForObject("http://item/{id}", Item.class, id);
}
```

## What is Zuul?

- Zuul is an API Gateway developed by Netflix
- it acts as a reverse proxy to handle routing, load balancing, authentication, logging, and security for micro-services
- basically, it's a way for end users to access all your micro-services from one single entry point

### The Zuul proxy annotation

Add the `@EnableZuulProxy` to have a Eureka client application act like a Zuul reverse proxy.

```java
@EnableZuulProxy
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

You can configure the behavior of the proxy in the `application.yml`.

```yml
zuul:
  ignored-services: "*"
  routes:
    item-service:
      path: /item/**
      serviceId: item
    cart-service:
      path: /cart/**
      serviceId: cart
```

- `ignored-services` tells Zuul not to automatically route services registered in the Eureka discovery network. By default, Zuul will route all `/<sericeId>/**` to that service
-  `routes` has the manually setup routes you want Zuul to filter requests to
  - `path` is the matching path to proxy the request to
  - `serviceId` is the ID of the service in Eureka discovery
  - you can also use `url` to give a fully qualified URL to proxy the request off to

#### `@EnableZuulProxy` versus `@EnableZuulServer`

- the `@EnableZuulProxy` tells Zuul that you want it to take care of Eureka stuff for you 
- this will automatically setup filters for you so that the `serviceId` in `routes` works correctly
-  `@EnableZuulServer` does not do this
- you can still specify `routes` in the application configuration, but that just tells Zuul that you want these requests to go trough your custom Zuul filters. An example would be

```yml
zuul:
  routes:
    integration: /integration/**
```

- where `/integration/**` would be filtered in a custom filter

#### Custom Zuul filters

- Zuul allows you make your own custom filters. There are 3 types of filters
  - `"pre"` which is good for things like authentication (to make sure the user can access to a service before it's given) and logging
  - `"route"` which is when the Zuul will actually route the request to a service
  - `"post"` which is good for populating header responses with other stuff you might want

##### Example of a custom Zuul filter

- here is an example of a `"route"` filter that filters `/integration/**` requests by their sub-domain
- the goal is to route certain requests with the name of item in their sub-domain instead of by item ID
- so `http://apple.localhost:8084/integration` should route to `http://localhost:8084/item/0` where `0` is the ID of the `apple` item

###### Custom configuration settings

To make this work, I am using some custom configurations in my `application.yml`.

```yml
integration-routing-configuration:
  serviceId: integration # the (dumby) service I'd like to apply the filter to
  forwardTo: item # the actual service I'd like to forward 
  map:
    apple: '0'
    banana: '1'
    orange: '2'
    bread: '3'
    eggs: '4'
    milk: '5'
    juice: '6'
```

And I am consuming these settings in this Spring bean by setting the `@ConfigurationProperties` annotation and giving the name of the property prefix I'd like to populate it.

```java
@Component
@ConfigurationProperties(prefix = "integration-routing-configuration")
@Data
public class IntegrationRoutingConfiguration {
    private Map<String, String> map;
    private String serviceId;
    private String forwardTo;
}
```

###### Custom filter

```java
@Component
public class IntegrationRoutingFilter extends ZuulFilter {

    @Autowired
    // we can auto wire the configuration as a bean
    private IntegrationRoutingConfiguration configuration; 

    @Autowired
    // we can auto wire the EurekaClient as a bean as well
    private EurekaClient eurekaClient; 

    @Override
    public String filterType() {
        return "route"; 
    }

    @Override
    public int filterOrder() {
        return 1; // lower numbers go first
    }

    @Override
    // this method tells Zuul whether or not to apply the filter
    public boolean shouldFilter() {
        // you can get the context of the request like this...
        RequestContext context = RequestContext.getCurrentContext();
        // Zuul (@EnableZuulProxy) will set this for us
        String serviceId = (String) context.get("serviceId");
        if (!serviceId.equals(configuration.getServiceId()))
            return false;
        // this helper will extract 'apple' from 'apple.localhost' or return null
        String subdomain = extractSubdomain(context);
        return subdomain != null && configuration.getMap().containsKey(subdomain);
    }

    @Override
    // this is the actual filter logic
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String subdomain = extractSubdomain(context);
        if (subdomain == null)
            return null;
        // get the client applications registered under the service ID we'd like to forward to (in this case the service ID is item)
        Application application =
                eurekaClient.getApplication(configuration.getForwardTo());
        // note that Eureka can have multiple instances under the same service (replication)
        List<InstanceInfo> instances = application.getInstances();
        if (instances.isEmpty()) {
            System.out.println("No instances available for: " + configuration.getForwardTo());
            return null;
        }
        // I am just picking up the first instance (since we only have 1 instance in this demo) but a load balancing client is also available to do this is a cooler way
        InstanceInfo instance = (InstanceInfo)instances.get(0);
        String requestURI = configuration.getMap().get(subdomain) + context.get("requestURI");
        String homePageUrl = instance.getHomePageUrl();
        try {
            URL url = new URL(homePageUrl + requestURI);
            System.out.println("URL: " + url);
            context.setRouteHost(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

## Running demo

```bash
# enter into the microservice project
cd ~/projects/microservice

# switch to Java version 8 (this is the default)
sdk use java 8.0.422-amzn 

# start up all the services
# it might take a minute or 2 before all the clients recognize each other
./run.sh

# you can tail the logs of a particular service with 'tail -f /tmp/<service>.log'
tail -f /tmp/gateway.log 

# you can check to see what PIDs are for each service if you want fine control or if you want to kill a specific service
cat /tmp/pids.txt 

# if you want to stop all the services use
./stop.sh
```

### Endpoints

- GET `http://localhost:8084/item`: get all the items
- GET `http://localhost:8084/item/[itemId]`: get an item by ID
- GET `http://localhost:8084/cart`
  - request body has a JSON list of item IDs
  - returns a list with the items inside
- GET `http://[itemName].localhost:8084/integration`: get an item by using its name as the sub-domain