# Spring Demo

> This is actively being worked on as I continue to learn more about Spring...

This is a demonstration for using Java, Maven, Spring Boot, Eureka, Zuul, and Caffeine to build a distributed web applications in a containerized environment. The only prerequisite for this project is having the Docker CLI properly setup on a Linux machine. This project is meant to be used alongside my notes [here](./notes).

## Usage

- you can execute `./build-run.sh` to both build the demo's image and to start the container
  - the container runs with port 8080 forwarded to the host

- to enter into a command line session, you can use `./enter.sh`
  - the `spring-demo-image` includes `zsh` and `oh-my-zsh`
  - you can refer to the `.zshrc` file in the root directory of this repository for additional quality-of-life plugins
- once inside the container's shell session, you can follow the *demo instructions* in the [notes directory](./notes)

## Tech stack

- base Image: `debian:12-slim`
- Z Shell
  - `zsh-autosuggestions`
  - `zsh-syntax-highlighting`
- SDKMAN with multiple Amazon Corretto JDK versions:
  - Java 8: `8.0.422-amzn` (default)
  - Java 11: `11.0.24-amzn`
  - Java 17: `17.0.12-amzn`
- caches remote development extensions in the `.vscode-server` directory
