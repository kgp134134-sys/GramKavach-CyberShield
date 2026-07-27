# Installation

## Prerequisites

- Android Studio Ladybug+ with JDK 17
- Android SDK Platform 35, Build Tools, and an Android 8.0 (API 26)+ device/emulator
- Internet access for the first Gradle dependency sync

## Steps

1. Clone or download the repository.
2. Open its root in Android Studio.
3. Sync Gradle. If SDK location is not configured, create a local `local.properties` through Android Studio.
4. Select `app` and run.

Run local tests with `./gradlew test` (or `gradlew.bat test` on Windows).

Do not add Bhashini credentials to source control. Create `~/.gradle/gradle.properties` with `BHASHINI_USER_ID`, `BHASHINI_API_KEY`, and, when supplied by your Bhashini account, `BHASHINI_BASE_URL`. The client sends them as `userID` and `ulcaApiKey` headers to the Bhashini pipeline endpoint.
