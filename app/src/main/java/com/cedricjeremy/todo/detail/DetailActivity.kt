package com.cedricjeremy.todo.detail

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cedricjeremy.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskDetailScreen()
        }
        val onValidate = {intent.putExtra("task", newTask)}
        setResult(RESULT_OK, intent)
    }
}

@Composable
fun TaskDetailScreen(onValidate: (Task) -> Unit) {
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
            val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
            finish()
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
