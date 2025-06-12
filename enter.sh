#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")
NAME="$(cat "$BASE_PATH/name.txt")"
docker exec -it "$NAME" zsh
