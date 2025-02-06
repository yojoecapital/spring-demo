#!/bin/bash

PID_FILE="/tmp/pids.txt"

stop-service() {
    local service_name=$1

    if [ -f "$PID_FILE" ]; then
        if [ -n "$service_name" ]; then
            pid=$(grep "^$service_name:" "$PID_FILE" | cut -d: -f2)
            if [ -n "$pid" ]; then
                echo "Stopping service $service_name:$pid."
                kill "$pid" && echo "Service $service_name stopped."
                grep -v "^$service_name:" "$PID_FILE" > "/tmp/pids_tmp.txt" && mv "/tmp/pids_tmp.txt" "$PID_FILE"
            else
                echo "Service '$service_name' not found."
            fi
        else
            while IFS=: read -r name pid; do
                echo "Stopping service $name:$pid."
                kill "$pid" && echo "Service $name stopped."
            done < "$PID_FILE"
            rm -f "$PID_FILE"
        fi
    else
        echo "No running services found."
    fi
}

stop-service "$1"
