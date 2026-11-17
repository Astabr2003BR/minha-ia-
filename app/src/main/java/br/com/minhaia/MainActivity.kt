package br.com.minhaia

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.minhaia.data.ChatDatabase
import br.com.minhaia.data.ChatMessage
import br.com.minhaia.data.LocalDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var chatDatabase: ChatDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatDatabase = ChatDatabase(applicationContext)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(database = chatDatabase)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(database: ChatDatabase) {
    val context = LocalContext.current
    val features = listOf("Chat", "Mangá", "Documentos", "Memória")
    var activeFeature by remember { mutableStateOf("Chat") }
    var draft by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var storageNotice by remember { mutableStateOf("Carregando dados locais...") }
    var memoryEnabled by remember { mutableStateOf(false) }
    var isWriting by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<LocalDocument?>(null) }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val documents = remember { mutableStateListOf<LocalDocument>() }
    val scope = rememberCoroutineScope()

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && !isWriting) {
            isWriting = true
            scope.launch {
                try {
                    val document = withContext(Dispatchers.IO) {
                        readTextDocument(context, uri)
                    }
                    withContext(Dispatchers.IO) { database.saveDocument(document) }
                    val updated = withContext(Dispatchers.IO) { database.loadDocuments() }
                    documents.clear()
                    documents.addAll(updated)
                    selectedDocument = updated.firstOrNull { it.id == document.id }
                    storageNotice = "Documento importado e salvo neste aparelho."
                } catch (error: Exception) {
                    storageNotice = error.message ?: "Não foi possível importar este documento."
                } finally {
                    isWriting = false
                }
            }
        }
    }

    LaunchedEffect(database) {
        try {
            val loaded = withContext(Dispatchers.IO) {
                Triple(database.loadMessages(), database.isMemoryEnabled(), database.loadDocuments())
            }
            messages.clear()
            messages.addAll(loaded.first)
            memoryEnabled = loaded.second
            documents.clear()
            documents.addAll(loaded.third)
            storageNotice = "Dados locais carregados."
        } catch (_: Exception) {
            storageNotice = "Falha ao carregar os dados locais."
        } finally {
            isLoading = false
        }
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Apagar histórico?") },
            text = { Text("Esta ação apaga as conversas armazenadas neste aparelho. Não apaga os documentos importados.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirmation = false
                    isWriting = true
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) { database.clearMessages() }
                            messages.clear()
                            storageNotice = "Histórico apagado deste aparelho."
                        } catch (_: Exception) {
                            storageNotice = "Não foi possível apagar o histórico."
                        } finally {
                            isWriting = false
                        }
                    }
                }) { Text("Apagar") }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Minha IA", style = MaterialTheme.typography.headlineLarge)
        Text("Ambiente local · sem conta", style = MaterialTheme.typography.labelMedium)

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            features.forEach { feature ->
                TextButton(onClick = { activeFeature = feature }) {
                    Text(if (activeFeature == feature) "• $feature" else feature)
                }
            }
        }

        when (activeFeature) {
            "Chat" -> {
                Text("Conversa", style = MaterialTheme.typography.titleLarge)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isLoading || messages.isEmpty()) {
                        item {
                            Text(if (isLoading) "Carregando..." else "Suas mensagens aparecerão aqui. O motor de IA ainda não foi conectado.")
                        }
                    }
                    items(messages, key = { it.id }) { message ->
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Você", style = MaterialTheme.typography.labelSmall)
                                Text(message.text)
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { if (it.length <= ChatDatabase.MAX_MESSAGE_LENGTH) draft = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Escreva uma mensagem") },
                        maxLines = 4
                    )
                    Button(
                        onClick = {
                            val clean = draft.trim()
                            if (clean.isNotEmpty() && !isLoading && !isWriting) {
                                val message = ChatMessage(UUID.randomUUID().toString(), clean, System.currentTimeMillis())
                                isWriting = true
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) { database.insertMessage(message) }
                                        messages.add(message)
                                        while (messages.size > ChatDatabase.MAX_MESSAGES) messages.removeAt(0)
                                        draft = ""
                                        storageNotice = "Mensagem salva neste aparelho."
                                    } catch (_: Exception) {
                                        storageNotice = "Falha ao salvar. A mensagem foi mantida no campo para nova tentativa."
                                    } finally {
                                        isWriting = false
                                    }
                                }
                            }
                        },
                        enabled = draft.isNotBlank() && !isLoading && !isWriting
                    ) { Text("Enviar") }
                }
                Text(storageNotice, style = MaterialTheme.typography.bodySmall)
                Text("Ainda não há respostas automáticas. O histórico é independente da memória configurável.", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { showClearConfirmation = true }, enabled = !isLoading && !isWriting) {
                    Text("Apagar histórico")
                }
            }
            "Documentos" -> {
                Text("Documentos locais", style = MaterialTheme.typography.headlineMedium)
                Text("Importe arquivos .txt ou .md. Limite de 1 MB por arquivo e 20 documentos salvos.", style = MaterialTheme.typography.bodySmall)
                Button(onClick = { documentPicker.launch(arrayOf("text/plain", "text/markdown")) }, enabled = !isLoading && !isWriting) {
                    Text(if (isWriting) "Importando..." else "Importar documento")
                }
                Text(storageNotice, style = MaterialTheme.typography.bodySmall)
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (documents.isEmpty()) item { Text("Nenhum documento importado.") }
                    items(documents, key = { it.id }) { document ->
                        TextButton(onClick = { selectedDocument = if (selectedDocument?.id == document.id) null else document }) {
                            Text(document.name)
                        }
                    }
                    selectedDocument?.let { document ->
                        item {
                            Text(document.name, style = MaterialTheme.typography.titleMedium)
                            Text(document.content, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Text("A leitura é apenas de texto. Nenhuma IA analisa o conteúdo ainda; PDF e OCR não estão disponíveis.", style = MaterialTheme.typography.bodySmall)
            }
            "Memória" -> {
                Column(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Memória configurável", style = MaterialTheme.typography.headlineMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Permitir memória futura")
                            Text("Quando o mecanismo de memória for conectado, esta opção controlará se ele poderá guardar informações.", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = memoryEnabled,
                            enabled = !isWriting,
                            onCheckedChange = { enabled ->
                                isWriting = true
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) { database.setMemoryEnabled(enabled) }
                                        memoryEnabled = enabled
                                        storageNotice = "Preferência de memória salva."
                                    } catch (_: Exception) {
                                        storageNotice = "Não foi possível salvar a preferência."
                                    } finally {
                                        isWriting = false
                                    }
                                }
                            }
                        )
                    }
                    Text("A memória inteligente ainda não está implementada. Desativar esta opção não apaga o histórico.", style = MaterialTheme.typography.bodySmall)
                    Text(storageNotice, style = MaterialTheme.typography.bodySmall)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(activeFeature, style = MaterialTheme.typography.headlineMedium)
                    Text("Este módulo ainda não está conectado. Nenhuma página de mangá ou imagem é gerada nesta versão.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun readTextDocument(context: Context, uri: Uri): LocalDocument {
    val name = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    } ?: throw IllegalArgumentException("Não foi possível identificar o nome do arquivo.")
    val lowerName = name.lowercase()
    require(lowerName.endsWith(".txt") || lowerName.endsWith(".md")) {
        "Formato não permitido. Selecione um arquivo .txt ou .md."
    }

    val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            require(total <= ChatDatabase.MAX_DOCUMENT_BYTES) { "O arquivo ultrapassa o limite de 1 MB." }
            output.write(buffer, 0, count)
        }
        output.toByteArray()
    } ?: throw IllegalArgumentException("Não foi possível abrir o arquivo.")

    val decoder = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
    val text = try {
        decoder.decode(ByteBuffer.wrap(bytes)).toString()
    } catch (_: Exception) {
        throw IllegalArgumentException("O arquivo não contém texto UTF-8 válido.")
    }
    require(text.isNotBlank()) { "O documento está vazio." }
    return LocalDocument(UUID.randomUUID().toString(), name.take(255), text, System.currentTimeMillis())
}
