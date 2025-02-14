#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")
CONTAINER_NAME="$(cat "$BASE_PATH/name.txt")-container"
docker exec -it "$CONTAINER_NAME" zsh
