#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")
USER_ID=$(id -u)
GROUP_ID=$(id -g)
USER="$(whoami)"

docker build --build-arg USER_ID=$USER_ID --build-arg GROUP_ID=$GROUP_ID --build-arg USER="$USER" -t spring-demo-image "$BASE_PATH"

# make sure .m2 exists
mkdir -p "$BASE_PATH/.m2"

docker run \
    --restart unless-stopped \
    --stop-timeout 1 \
    -v "$BASE_PATH/projects:/home/$USER/workspace" \
    -v "$BASE_PATH/.m2:/home/$USER/.m2" \
    -v "$BASE_PATH/.zshrc:/home/$USER/.zshrc" \
    -d \
    --name spring-demo-container \
    spring-demo-image tail -f /dev/null

