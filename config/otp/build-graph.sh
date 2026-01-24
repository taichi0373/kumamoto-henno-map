#!/bin/bash
set -e

docker run --rm \
  -v "$(pwd)/data":/var/opentripplanner \
  docker.io/opentripplanner/opentripplanner:2.5.0 \
  --build --save
