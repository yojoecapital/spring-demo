#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")

docker build -t spring-demo-image "$BASE_PATH"

docker run \
    --restart unless-stopped \
    -v "$BASE_PATH/project:/workspace" \
    -v "$BASE_PATH/.m2:/root/.m2" \
    -v "$BASE_PATH/.zshrc:/root/.zshrc" \
    -w /workspace \
    -p 8080:8080 \
    -d \
    --name spring-demo-container \
    spring-demo-image tail -f /dev/null

