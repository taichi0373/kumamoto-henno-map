#!/bin/bash
set -e

docker run --rm \
  -e JAVA_TOOL_OPTIONS="-Xmx4500m" \
  -v "$(pwd)/data":/var/opentripplanner \
  docker.io/opentripplanner/opentripplanner:2.5.0 \
  --build --save
