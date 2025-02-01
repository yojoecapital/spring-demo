#!/bin/bash

set -e

run-service() {
    cd "$HOME/projects/microservice/$1"
    mvn clean install
    mvn spring-boot:run > "/tmp/$1.log" &
    echo $! >> /tmp/service_pids.txt  # Save PID of the service
}

run-service discovery
run-service gateway
run-service item
run-service cart
