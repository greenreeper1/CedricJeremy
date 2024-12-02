package com.cedricjeremy.todo.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskDetailScreen()
        }
    }
}

@Composable
fun TaskDetailScreen() {
    // Contenu principal de l'écran
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Task Detail",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Title",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Description",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = {
            // Ajoutez ici une logique si nécessaire
        }) {
            Text(text = "Validate")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TaskDetailScreen()
}
