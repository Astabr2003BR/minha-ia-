# Build setup — initial scaffold

## Android

The Android module currently targets:
- Android Gradle Plugin: 9.4.0 (stable release line)
- Gradle: 9.6.0 minimum for AGP 9.4
- JDK: 17
- Android compile SDK: 36
- Kotlin support: built into AGP 9.x
- Compose compiler plugin: Kotlin 2.4.21
- Compose BOM: 2026.09.00

Use an Android Studio release that supports AGP 9.4 and Gradle 9.6. The repository does not yet include the Gradle Wrapper scripts/JAR, so wrapper generation and a real sync/build are still required before claiming Android build success.

## Web

- Node.js: use a currently supported LTS release compatible with Vite 7.
- Install dependencies from the `web/` directory with `npm install`.
- Run the development server with `npm run dev`.
- Run the production compile with `npm run build`.

## Validation status

No Gradle sync, Android build, npm install, TypeScript check, Vite build, browser test or physical-device test has been executed from this repository during this phase. The versions above were checked against current official documentation on 2026-11-17; actual dependency resolution remains pending.

## Important

The current chat UI is a local interaction prototype. It stores messages in process memory only, does not call a model, and loses messages when the app/page session is restarted.
