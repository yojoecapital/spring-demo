#!/bin/bash

set -e

# Path to store PIDs
PID_FILE="/tmp/service_pids.txt"

# Function to stop a service by PID
stop-service() {
    local pid=$1
    if [ -n "$pid" ]; then
        echo "Stopping service with PID $pid"
        kill "$pid" && echo "Service stopped."
    else
        echo "No PID found for this service."
    fi
}

# Stop services listed in the PID file
if [ -f "$PID_FILE" ]; then
    while IFS= read -r pid; do
        stop-service "$pid"
    done < "$PID_FILE"
    rm "$PID_FILE"
else
    echo "No running services found."
fi
