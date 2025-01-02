# Spring Demo

> This is actively being worked on as I continue to learn more about Spring...

This is a demonstration for using Java, Maven, and Spring Boot to build a simple MVC web application in a containerized environment. The only prerequisite for this project is having the Docker CLI properly setup on a Linux machine. This project is meant to be used alongside my notes [here](./notes).

## Usage

- you can execute `./build-run.sh` to both build the demo's image and to start the container
  - the container runs with port 8080 forwarded to the host

- to enter into a command line session, you can use `./enter.sh`
  - the `spring-demo-image` includes `zsh` and `oh-my-zsh`
  - you can refer to the `.zshrc` file in the root directory of this repository for additional quality-of-life plugins
- inside the container's shell session, you can execute the following to run each of the demos

```bash
# running the POJ demos
java -cp target/classes/ com.learning.poj.LooselyCoupled 
java -cp target/classes/ com.learning.poj.TightlyCoupled 

# running the Spring demo
mvn exec:java -Dexec.mainClass="com.learning.spring.Application"

# running the Spring Boot demo
mvn exec:java -Dexec.mainClass="com.learning.springboot.Application"

# running the Spring Boot MVC demo
mvn spring-boot:run
# OR
mvn exec:java -Dexec.mainClass="com.learning.mvc.Application"
```

