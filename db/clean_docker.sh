#!/bin/sh
set -e

cd "$(dirname "$0")/.."
docker compose down --rmi all -v --remove-orphans
docker system prune -af
