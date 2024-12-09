package com.cedricjeremy.todo.detail

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cedricjeremy.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskDetailScreen(onValidate = {newTask ->
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish() })
        }
    }
}

@Composable
fun TaskDetailScreen(onValidate: (Task) -> Unit) {
    // Contenu principal de l'écran
    var task by remember { mutableStateOf(Task(id = UUID.randomUUID().toString(), title = "New Task !")) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        OutlinedTextField(
            value = task.title,
            onValueChange = {task.id},
            label = {Text("Create a new task")}
        )
        OutlinedTextField(
            value = title,
            onValueChange = {title = it},
            label = {Text("Title")}
        )
        OutlinedTextField(
            value = desc,
            onValueChange = {desc = it},
            label = {Text("Description")}
        )
        Button(onClick = {
            // Ajoutez ici une logique si nécessaire
            val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task!")
        }) {
            Text(text = "Validate")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TaskDetailScreen(onValidate = {newTask ->
        })
}
