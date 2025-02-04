#!/bin/bash

PID_FILE="/tmp/pids.txt"

stop-service() {
    local pid=$1
    if [ -n "$pid" ]; then
        echo "Stopping service with PID $pid"
        kill "$pid" && echo "Service stopped."
    else
        echo "No PID found for this service."
    fi
}

if [ -f "$PID_FILE" ]; then
    while IFS=: read -r name pid; do
        stop-service "$pid"
    done < "$PID_FILE"
    rm "$PID_FILE"
else
    echo "No running services found."
fi
