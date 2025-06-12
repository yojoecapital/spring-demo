#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")
USER_ID=$(id -u)
GROUP_ID=$(id -g)
USER="$(whoami)"
NAME="$(cat "$BASE_PATH/name.txt")"
IMAGE_NAME="$NAME-image"

docker build --build-arg USER_ID=$USER_ID --build-arg GROUP_ID=$GROUP_ID --build-arg USER="$USER" -t "$IMAGE_NAME" "$BASE_PATH"

mkdir -p "$BASE_PATH/m2"
mkdir -p "$BASE_PATH/vscode-server"

docker run \
    --restart unless-stopped \
    --stop-timeout 1 \
    -v "$BASE_PATH/projects:/home/$USER/projects" \
    -v "$BASE_PATH/m2:/home/$USER/.m2" \
    -v "$BASE_PATH/zsh/.zshrc:/home/$USER/.zshrc" \
    -v "$BASE_PATH/vscode-server:/home/$USER/.vscode-server" \
    --net=host \
    -d \
    --name "$NAME" \
    --hostname "$NAME" \
    "$IMAGE_NAME" tail -f /dev/null

echo Running $NAME...

OPENAM_NAME="openam-$NAME"
OPENAM_SERVER_URL="http://localhost:8080/openam"

docker run -it --name "$OPENAM_NAME" \
  -p 8080:8080 \
  -e SERVER_URL="$OPENAM_SERVER_URL" \
  -e AM_SETUP=true \
  -d \
  ghcr.io/openidentityplatform/openam/openam:14.5.4

echo Running $OPENAM_NAME on $OPENAM_SERVER_URL...
