# 2. Using Spring without Boot

It's useful to know how Spring operates without all the niceties of Spring Boot such as the annotation system.

> These notes will use the code in the `springboot` project.

## Application Context and Bean Factory

- Spring manages beans in the IoC container which is represented by the `ApplicationContext` interface
- the `ApplicationContext` is an extension of the `BeanFactory` interface
- the `BeanFactory` interface primarily focuses on bean instantiation and dependency resolution
- the`ApplicationContext` interface extends `BeanFactory` with additional features like event handling and aspect oriented programming support
- one implementation of `ApplicationContext`  is `XmlApplicationContext` which uses a concrete implementation of `BeanFactory` to manage beans through XML configurations

### Example

#### Configuration

```xml
<!-- src/main/resources/spring.xml -->
<beans ...>
    <!-- autowiring by constructor will match the types of Mario for gb's constructor -->
    <bean id="gb" class="com.learning.springboot.game.Gameboy" autowire="constructor"></bean>
    <bean id="gba" class="com.learning.springboot.game.GameboyAdvance">
        <!-- using a property will find gba's setter for game and assign to it the bean with id "sonic" -->
        <property name="game" ref="sonic"></property>
    </bean>
    <bean id="mario" primary="true" class="com.learning.springboot.game.Mario"></bean>
    <bean id="sonic" class="com.learning.springboot.game.Sonic"></bean>
</beans>
```

#### Main class

```java
try (var context = new ClassPathXmlApplicationContext("spring.xml")) {
    var gameboyAdvance = context.getBean(GameboyAdvance.class);
    gameboyAdvance.play();
    var gameboy = context.getBean(Gameboy.class);
    gameboy.play();
}
```

## Running demo

```bash
cd ~/projects/springboot
sdk use java 17.0.12-amzn 
mvn compile

# running the Spring demo
mvn exec:java -Dexec.mainClass="com.learning.spring.Application"
# use ^C to stop the program (it's running on an embedded tomcat server)
```

