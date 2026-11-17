package br.com.minhaia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import br.com.minhaia.data.ChatDatabase
import br.com.minhaia.data.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onDestroy() {
        if (::chatDatabase.isInitialized) chatDatabase.close()
        super.onDestroy()
    }
}

@Composable
private fun HomeScreen(database: ChatDatabase) {
    val features = listOf("Chat", "Mangá", "Documentos", "Memória")
    var activeFeature by remember { mutableStateOf("Chat") }
    var draft by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var storageNotice by remember { mutableStateOf("Carregando histórico local...") }
    var memoryEnabled by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(database) {
        try {
            val loaded = withContext(Dispatchers.IO) {
                database.loadMessages() to database.isMemoryEnabled()
            }
            messages.clear()
            messages.addAll(loaded.first)
            memoryEnabled = loaded.second
            storageNotice = "Histórico salvo neste aparelho."
        } catch (_: Exception) {
            storageNotice = "Falha ao carregar o histórico local."
        } finally {
            isLoading = false
        }
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Apagar histórico?") },
            text = { Text("Esta ação apaga as conversas armazenadas neste aparelho. Não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirmation = false
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) { database.clearMessages() }
                            messages.clear()
                            storageNotice = "Histórico apagado deste aparelho."
                        } catch (_: Exception) {
                            storageNotice = "Não foi possível apagar o histórico."
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
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
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
                            Text(
                                if (isLoading) "Carregando..." else
                                    "Suas mensagens aparecerão aqui. O motor de IA ainda não foi conectado."
                            )
                        }
                    }
                    items(messages, key = { it.id }) { message ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Você", style = MaterialTheme.typography.labelSmall)
                                Text(message.text)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            if (clean.isNotEmpty() && !isLoading) {
                                val message = ChatMessage(
                                    id = UUID.randomUUID().toString(),
                                    text = clean,
                                    createdAt = System.currentTimeMillis()
                                )
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) { database.insertMessage(message) }
                                        messages.add(message)
                                        while (messages.size > ChatDatabase.MAX_MESSAGES) messages.removeAt(0)
                                        draft = ""
                                        storageNotice = "Mensagem salva neste aparelho."
                                    } catch (_: Exception) {
                                        storageNotice = "Falha ao salvar. A mensagem foi mantida no campo para nova tentativa."
                                    }
                                }
                            }
                        },
                        enabled = draft.isNotBlank() && !isLoading
                    ) { Text("Enviar") }
                }
                Text(storageNotice, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Ainda não há respostas automáticas. O histórico é independente da memória configurável.",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { showClearConfirmation = true }, enabled = !isLoading) {
                    Text("Apagar histórico")
                }
            }
            "Memória" -> {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Memória configurável", style = MaterialTheme.typography.headlineMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Permitir memória futura")
                            Text(
                                "Quando o mecanismo de memória for conectado, esta opção controlará se ele poderá guardar informações.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = memoryEnabled,
                            onCheckedChange = { enabled ->
                                memoryEnabled = enabled
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) { database.setMemoryEnabled(enabled) }
                                        storageNotice = "Preferência de memória salva."
                                    } catch (_: Exception) {
                                        memoryEnabled = !enabled
                                        storageNotice = "Não foi possível salvar a preferência."
                                    }
                                }
                            }
                        )
                    }
                    Text(
                        "Importante: a memória inteligente ainda não está implementada. Desativar esta opção não apaga o histórico de conversas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(storageNotice, style = MaterialTheme.typography.bodySmall)
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(activeFeature, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Este módulo ainda não está conectado. Nenhum documento é processado e nenhuma imagem é gerada nesta versão.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
