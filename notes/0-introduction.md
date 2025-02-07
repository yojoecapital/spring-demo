# 0. Introduction

- you can build a variety of applications with Java, Spring, and Spring Boot
- the Spring Framework provides all the core features
- some terminology that will be used:
	1. tightly and loose coupling
	2. IoC container
	3. application context
	4. component scan
	5. dependency injection (DI)
	6. Spring beans
	7. auto wiring 
	8. micro-services
	9. service discovery
	10. cache

## Materials

- https://www.youtube.com/watch?v=f6DHAgL7FWc
- https://www.youtube.com/playlist?list=PLsyeobzWxl7qbKoSgR5ub6jolI8-ocxCF
- https://www.youtube.com/playlist?list=PLqq-6Pq4lTTZSKAFG6aCDVDP86Qx4lNas
- https://www.baeldung.com/java-caching-caffeine
- https://www.baeldung.com/spring-boot-caffeine-cache

## Basic tips on running Maven projects

### Common Maven goals
| **Command**           | **Purpose**                                      |
|------------------------|--------------------------------------------------|
| `mvn compile`         | Compiles the project.                           |
| `mvn clean`           | Deletes the `target` directory.                 |
| `mvn package`         | Packages the project into a `.jar` or `.war`.    |
| `mvn test`            | Runs the unit tests.                            |
| `mvn install`         | Installs the project artifact into your local repo. |
| `mvn clean install`   | Cleans, compiles, tests, and installs in one go. |

### Running with Spring Boot

```bash
# this will look for the class annotated with @SpringBootApplication
mvn spring-boot:run
```

### Packaging the project

```bash
# clean the project
mvn clean

# compile the project
mvn compile

# run unit tests
mvn test

# package the application (i.e. build a fat JAR in target)
mvn package

# run the packaged application
java -jar target/your-project-name-version.jar
```

### Running without packaging

```bash
mvn compile

# -D is used to pass down arguments or override pom.xml
mvn exec:java -Dexec.mainClass=package.name.Class

# note that using java directly will not use Maven packages as they are only included in the fat JAR
java -cp target/classes package.name.Class
```

