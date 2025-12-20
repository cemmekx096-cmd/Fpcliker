#!/bin/bash
# Script to generate Gradle wrapper files

# Download gradle wrapper jar
mkdir -p gradle/wrapper
curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar

# Download gradlew
curl -L -o gradlew https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradlew
chmod +x gradlew

# Download gradlew.bat
curl -L -o gradlew.bat https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradlew.bat

echo "Gradle wrapper files generated successfully!"
