#!/usr/bin/env sh
# Gradle wrapper bootstrap
DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="$DIR"
CLASSPATH="$APP_HOME/gradle/wrapper"
JAR="$CLASSPATH/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  echo "gradle-wrapper.jar missing — running via system gradle or download."
fi
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
