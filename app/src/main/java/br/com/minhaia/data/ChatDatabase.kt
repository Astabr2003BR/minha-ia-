package br.com.minhaia.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ChatMessage(
    val id: String,
    val text: String,
    val createdAt: Long
)

data class LocalDocument(
    val id: String,
    val name: String,
    val content: String,
    val createdAt: Long
)

class ChatDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val lock = Any()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE messages (
                id TEXT PRIMARY KEY NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE app_settings (
                setting_key TEXT PRIMARY KEY NOT NULL,
                setting_value TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE documents (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "INSERT INTO app_settings(setting_key, setting_value) VALUES(?, ?)",
            arrayOf(KEY_MEMORY_ENABLED, "0")
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS app_settings (
                    setting_key TEXT PRIMARY KEY NOT NULL,
                    setting_value TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "INSERT OR IGNORE INTO app_settings(setting_key, setting_value) VALUES(?, ?)",
                arrayOf(KEY_MEMORY_ENABLED, "0")
            )
        }
        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS documents (
                    id TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    content TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun loadMessages(): List<ChatMessage> = synchronized(lock) {
        val result = mutableListOf<ChatMessage>()
        readableDatabase.query(
            "messages", arrayOf("id", "content", "created_at"),
            null, null, null, null, "created_at ASC", MAX_MESSAGES.toString()
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val textIndex = cursor.getColumnIndexOrThrow("content")
            val timeIndex = cursor.getColumnIndexOrThrow("created_at")
            while (cursor.moveToNext()) {
                result += ChatMessage(cursor.getString(idIndex), cursor.getString(textIndex), cursor.getLong(timeIndex))
            }
        }
        result
    }

    fun insertMessage(message: ChatMessage) = synchronized(lock) {
        require(message.id.isNotBlank())
        require(message.text.isNotBlank() && message.text.length <= MAX_MESSAGE_LENGTH)
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "INSERT INTO messages(id, content, created_at) VALUES(?, ?, ?)",
                arrayOf(message.id, message.text, message.createdAt)
            )
            db.execSQL(
                """
                DELETE FROM messages WHERE id NOT IN (
                    SELECT id FROM messages ORDER BY created_at DESC, rowid DESC LIMIT ?
                )
                """.trimIndent(),
                arrayOf(MAX_MESSAGES)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun clearMessages() = synchronized(lock) {
        writableDatabase.delete("messages", null, null)
    }

    fun loadDocuments(): List<LocalDocument> = synchronized(lock) {
        val result = mutableListOf<LocalDocument>()
        readableDatabase.query(
            "documents", arrayOf("id", "name", "content", "created_at"),
            null, null, null, null, "created_at DESC", MAX_DOCUMENTS.toString()
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += LocalDocument(
                    cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3)
                )
            }
        }
        result
    }

    fun saveDocument(document: LocalDocument) = synchronized(lock) {
        require(document.id.isNotBlank())
        require(document.name.isNotBlank() && document.name.length <= 255)
        require(document.content.isNotBlank())
        require(document.content.toByteArray(Charsets.UTF_8).size <= MAX_DOCUMENT_BYTES)
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "INSERT OR REPLACE INTO documents(id, name, content, created_at) VALUES(?, ?, ?, ?)",
                arrayOf(document.id, document.name, document.content, document.createdAt)
            )
            db.execSQL(
                """
                DELETE FROM documents WHERE id NOT IN (
                    SELECT id FROM documents ORDER BY created_at DESC, rowid DESC LIMIT ?
                )
                """.trimIndent(),
                arrayOf(MAX_DOCUMENTS)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun isMemoryEnabled(): Boolean = synchronized(lock) {
        readableDatabase.query(
            "app_settings", arrayOf("setting_value"), "setting_key = ?",
            arrayOf(KEY_MEMORY_ENABLED), null, null, null, "1"
        ).use { cursor -> cursor.moveToFirst() && cursor.getString(0) == "1" }
    }

    fun setMemoryEnabled(enabled: Boolean) = synchronized(lock) {
        writableDatabase.execSQL(
            "INSERT OR REPLACE INTO app_settings(setting_key, setting_value) VALUES(?, ?)",
            arrayOf(KEY_MEMORY_ENABLED, if (enabled) "1" else "0")
        )
    }

    companion object {
        private const val DATABASE_NAME = "minha_ia.db"
        private const val DATABASE_VERSION = 3
        const val MAX_MESSAGES = 300
        const val MAX_MESSAGE_LENGTH = 10_000
        const val MAX_DOCUMENTS = 20
        const val MAX_DOCUMENT_BYTES = 1_048_576
        private const val KEY_MEMORY_ENABLED = "memory_enabled"
    }
}
