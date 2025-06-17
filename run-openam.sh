#!/bin/bash

BASE_PATH=$(dirname "$(realpath "$0")")
NAME="$(cat "$BASE_PATH/name.txt")"
OPENAM_NAME="openam-$NAME"
OPENAM_SERVER_URL="http://localhost:8080/openam"

docker run -it --rm --name "$OPENAM_NAME" \
  -p 8080:8080 \
  -e SERVER_URL="$OPENAM_SERVER_URL" \
  -e AM_SETUP=true \
  -d \
  ghcr.io/openidentityplatform/openam/openam:14.5.4

echo Running $OPENAM_NAME on $OPENAM_SERVER_URL...
