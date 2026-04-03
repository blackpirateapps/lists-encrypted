#!/usr/bin/env bash

DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "Missing gradle wrapper jar. Please add gradle-wrapper.jar." >&2
  exit 1
fi

java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
