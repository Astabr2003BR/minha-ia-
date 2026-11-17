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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
private fun HomeScreen() {
    val features = listOf("Chat", "Mangá", "Documentos", "Memória")
    var activeFeature by remember { mutableStateOf("Chat") }
    var draft by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Minha IA", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Ambiente local · sem conta",
            style = MaterialTheme.typography.labelMedium
        )

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

        if (activeFeature == "Chat") {
            Text(
                "Conversa",
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Text(
                            "Suas mensagens aparecerão aqui. O motor de IA ainda não foi conectado.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                items(messages) { message ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Escreva uma mensagem") },
                    maxLines = 4
                )
                Button(
                    onClick = {
                        val cleanMessage = draft.trim()
                        if (cleanMessage.isNotEmpty()) {
                            messages.add(cleanMessage)
                            draft = ""
                        }
                    },
                    enabled = draft.isNotBlank()
                ) {
                    Text("Enviar")
                }
            }
            Text(
                "Protótipo: as mensagens ficam somente nesta sessão e não recebem resposta automática.",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(activeFeature, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Este módulo será implementado em uma etapa própria. Nenhum dado é enviado ou salvo ainda.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
