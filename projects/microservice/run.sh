#!/bin/bash

set -e

run-service() {
    local service_dir="$HOME/projects/microservice/$1"
    if [ ! -d "$service_dir" ]; then
        echo "Service $service_dir does not exist." >&2
        exit 1
    fi
    cd "$HOME/projects/microservice/$1" || exit 1
    mvn clean package install
    mvn spring-boot:run > "/tmp/$1.log" 2>&1 &
    echo "$1:$!" >> /tmp/pids.txt 
}

if [ -z "$1" ]; then  
    run-service discovery
    run-service gateway
    run-service item
    run-service cart
else
    run-service "$1"
fi
