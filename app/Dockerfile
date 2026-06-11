FROM --platform=linux/amd64 ghcr.io/cirruslabs/android-sdk:36 AS dev

WORKDIR /workspace

ENV GRADLE_USER_HOME=/root/.gradle

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY app ./app

RUN chmod +x ./gradlew

CMD ["./gradlew", ":app:assembleDebug"]
