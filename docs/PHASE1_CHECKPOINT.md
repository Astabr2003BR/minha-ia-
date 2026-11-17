# Phase 1 — local persistence and configurable-memory preference checkpoint

## Branch
`feature/phase1-scaffold`

## Implemented in this checkpoint
- Android chat history persisted in a private SQLite database through `SQLiteOpenHelper`.
- Android message insertion uses a transaction and prunes history to the latest 300 messages.
- Android message validation: non-empty ID/text and a 10,000-character maximum.
- Android memory preference stored locally, default off.
- Android UI can toggle the future-memory preference and explicitly clear chat history after confirmation.
- Android database operations run on `Dispatchers.IO`; the UI disables conflicting writes while a mutation is active.
- Web chat history persisted in IndexedDB.
- Web IndexedDB schema upgraded to version 2 with a separate settings object store.
- Web memory preference persisted locally, default off.
- Web UI can toggle the future-memory preference and clear chat history after confirmation.
- Web writes are serialized through a queue; malformed records are rejected.
- Web history read failure prevents the session from overwriting the existing stored conversation.
- Fixed malformed literal escape in `web/src/App.tsx` that would prevent TypeScript parsing.
- Updated CSS and architecture documentation.

## Explicitly not implemented yet
- The memory preference is only a consent/configuration flag. There is no automatic memory extraction, retrieval, editing or deletion because no model/memory engine is connected.
- No assistant response generation or local LLM runtime is connected.
- No model downloader, model catalogue, license verification or SHA-256 download verification is implemented.
- No PDF parser/OCR or document indexing is implemented.
- No image-generation runtime is implemented.
- No manga page/character/scene generator is implemented.
- Android and Web data are not synchronized.
- Web IndexedDB and Android SQLite are not encrypted by this app.
- No multi-user accounts or server-side isolation are implemented.

## Limits
- 300 chat messages per local conversation.
- 10,000 characters per message.
- Web data stays in the browser origin's IndexedDB.
- Android data stays in the app's private SQLite database.
- Clearing browser site data or uninstalling/clearing Android app data can remove local content.

## Validation evidence
- The changed Android, Web, storage and CSS files were fetched back from the GitHub branch after commits.
- Static content checks confirmed the IndexedDB version 2 stores, serialized write queue, memory preference operations, history deletion controls, and Android SQLite/history/memory code are present.
- The malformed literal backslash-n sequence is no longer present in the Web App source.
- Attempted to clone the branch into a test container to run `npm install` and `npm run build`; the attempt was blocked because the container could not resolve `github.com`. No npm install or build was executed.
- No Gradle sync/build, Android unit/instrumentation test, browser runtime test, or POCO X6 5G test has been executed.
- The GitHub CI workflow exists, but no successful CI result has been confirmed for this checkpoint.

## Build setup gap
Gradle Wrapper scripts/JAR are not included yet. The GitHub Actions workflow uses Gradle 9.6.0 directly. A real Android build and device test remain required.

## Security notes
- No API keys, credentials, personal documents or model weights were added.
- Android app backup remains disabled.
- The public repository must never contain secrets, private documents, personal chat exports or model weights without explicit license review.
- Local persistence is not equivalent to encryption. Do not use this prototype as a secure vault.
- The memory preference does not affect or delete chat history.
