# Build setup — initial scaffold

## Android

The Android module currently targets:
- Android Gradle Plugin: 9.4.0
- Gradle: 9.6.0 minimum for AGP 9.4
- JDK: 17
- Android compile SDK: 36
- Kotlin support: built into AGP 9.x
- Compose compiler plugin: Kotlin 2.4.21
- Compose BOM: 2026.09.00
- Unit tests: JUnit 4.13.2

The repository currently does not contain Gradle Wrapper scripts/JAR. CI installs Gradle 9.6.0 directly. A reproducible local build workflow still requires adding and verifying the official Gradle Wrapper files. Do not generate wrapper scripts/JAR by hand; use the official Gradle wrapper task once a controlled Gradle 9.6.0 environment is authorized.

A first JUnit contract test file now covers generation-option bounds and blank prompt rejection. These tests have not been executed.

No Android build has been run after the dependency correction to `activity-compose:1.13.0` and `lifecycle-runtime-ktx:2.11.0`. Do not treat dependency declarations as proof of a successful build.

## Web

- Node.js: use a currently supported LTS release compatible with Vite 7.
- Install dependencies from the `web/` directory with `npm install`.
- Run the development server with `npm run dev`.
- Run the production compile with `npm run build`.

## Current implementation facts

- Android chat history is persisted in private SQLite (up to 300 messages).
- Web chat history is persisted in IndexedDB (up to 300 messages).
- Android and Web each import TXT/Markdown locally, with UTF-8 validation and a 1 MiB per-file limit.
- Memory is only a local preference flag, default off; intelligent memory is not implemented.
- The chat UI does not call a model and does not generate automatic AI responses.
- Android and Web storage are independent; no synchronization exists.

## Validation status

- A prior GitHub Actions run completed the Web dependency installation and production build successfully.
- That run's Android build failed during dependency resolution before Kotlin compilation; the dependency versions were subsequently corrected, but the corrected Android build has not been executed.
- No browser runtime, Android emulator, physical-device or POCO X6 5G test has been executed in this continuation.
- CI is manual-only. No build was started in this continuation.
