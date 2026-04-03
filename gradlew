#!/usr/bin/env bash

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_SHARED_JAR="$DIR/gradle/wrapper/gradle-wrapper-shared.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Missing gradle wrapper jar. Please add gradle-wrapper.jar." >&2
  exit 1
fi

if [ ! -f "$WRAPPER_SHARED_JAR" ]; then
  echo "Missing gradle wrapper shared jar. Please add gradle-wrapper-shared.jar." >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java is required to run Gradle. Install JDK 17+." >&2
  exit 1
fi

exec java -classpath "$WRAPPER_JAR:$WRAPPER_SHARED_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
