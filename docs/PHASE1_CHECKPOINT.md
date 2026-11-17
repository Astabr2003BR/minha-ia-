# Phase 1 — local persistence and document import checkpoint

## Branch
feature/phase1-scaffold

## Implemented
- Android chat history persisted in private SQLite; 300-message limit and 10,000-character limit.
- Android memory preference stored locally, default off; this is only a future-consent flag.
- Web chat history persisted in IndexedDB; writes serialized and malformed records rejected.
- Web memory preference stored locally, default off.
- Android and Web import plain-text .txt and Markdown .md files into local storage.
- Android uses the system document picker (Storage Access Framework); no broad storage permission was added.
- Imported files are read as UTF-8, non-empty, and limited to 1 MiB each.
- Local document library retains up to 20 documents; Android uses SQLite and Web uses IndexedDB.
- Document content is rendered as text, not HTML; no scripts, macros, links or embedded content are executed.
- Document import and chat history remain separate; deleting chat history does not delete imported documents.
- Explicit Android database migration from version 2 to version 3 adds the documents table without destructive reset.
- Web IndexedDB schema version 3 adds a documents object store; old stores are retained.

## Not implemented
- No local LLM or assistant response generation.
- No document question answering, semantic indexing, chunking or retrieval.
- No PDF parsing, OCR, image extraction or Office document support.
- No image generation or manga page generation.
- No intelligent memory extraction, retrieval, editing or deletion.
- No Android/Web synchronization, accounts or server-side multi-user isolation.
- Local databases are not encrypted by this app.

## Limits
- 300 chat messages; 10,000 characters per message.
- 20 local documents; 1 MiB per document.
- Android and Web storage are independent and do not synchronize.
- Clearing browser data or uninstalling/clearing app data can remove local content.

## Validation evidence
- Source files were committed to this branch and fetched back for review.
- Static review confirmed Android database schema version 3 and explicit migration from earlier versions.
- Static review confirmed Web IndexedDB schema version 3, separate document store, UTF-8 validation, 1 MiB limit and deterministic pruning of oldest documents.
- No npm install, TypeScript build, Vite build, Gradle build, emulator test, browser runtime test or POCO X6 5G test has been executed in this environment.
- GitHub Actions status must be checked separately; no successful build is claimed.

## Security notes
- No broad Android storage permission was added; file access is user-initiated through the system picker.
- Imported content is treated as untrusted plain text and never interpreted as HTML or executable instructions.
- No API keys, credentials, private documents or model weights were committed.
- Local persistence is not encryption and must not be treated as a secure vault.
