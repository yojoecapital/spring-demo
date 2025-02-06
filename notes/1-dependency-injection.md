# Dependency injection

- traditionally, managing each POJO (plain old Java object) is completely in the hands of the developer
  - the POJO exist logically in the JVM (Java virtual machine)
- the Spring Framework introduces IoC (i**nversion of control**) where it becomes the responsibility of Spring to manage objects and their dependencies
- DI (**dependency injection**) is an implementation of IoC
- objects that Spring manages are called **Spring beans** 
  - spring beans exist logically in the spring container (or IoC container, or spring context) which exists in the JVM

> These notes will use the code in the `springboot` project.

## Spring beans with Spring Boot

```java
// you can label a POJO as a spring bean with the @Component annotation
@Component
public class Gameboy {
    Mario game;

    public Gameboy(Mario game) {
        this.game = game;
    }

    public void play() {
        System.out.println("Now playing: \"" + game + "\"");
        game.jump();
    }
}

// the Mario game is also a bean so that Spring knows to inject it into Gameboy's contructor
@Component
public class Mario implements Game {
    public void jump() {
        System.out.println("Mario is jumping!");
    }

    @Override
    public String toString() {
        return "Super Mario Bros. 1985";
    }
}
```

### How do I get my bean?

```java
public static void main(String[] args) {
    // context is a (Configurable) ApplicationContext
    var context = SpringApplication.run(Application.class, args);
    var gameboy = context.getBean(Gameboy.class);
    gameboy.play();
}
```

### Constructor inject, setter injection, and field injection

```java
// this is an example of constructor injection
@Autowired
public Gameboy(Mario game) {
    this.game = game;
}

// this is an example of setter injection
@Autowired
public setGame(Mario game) {
    this.game = game;
}

// this is an example of field injection
@Autowired
private Mario game;
```

- the `@Autowired` annotation let's Spring know that it should manage that dependency with injection
- auto wiring can be implied for constructors of beans
- notice that Spring can also manage private fields with injection. This is because Spring uses Java's reflection API so it can bypass the usual visibility modifiers
- it isn't recommended to use private field injection since it can make debugging a bit more difficult

### Resolving ambiguity

- it is possible that the required injection in ambiguous as a result of polymorphism (like with 2 different games implementing the same `Game` interface)
- you can `@Primary` on a component to let Spring know this bean takes priority in cases of ambiguity
- you can also use `@Qualifer(String name)` to specify the name for an DI
  - by default, the name of a component is class name but with the first letter in lowercase\

```java
@Autowired
@Qualifier("sonic")
public void setGame(Game game) {
    this.game = game;
}
```

## Running demo

```bash
# enter into the springboot project
cd ~/projects/springboot

# use Java 17
sdk use java 17.0.12-amzn 

# compile the project
mvn compile

# running the POJ demos
java -cp target/classes/ com.learning.poj.LooselyCoupled 
java -cp target/classes/ com.learning.poj.TightlyCoupled 
```



