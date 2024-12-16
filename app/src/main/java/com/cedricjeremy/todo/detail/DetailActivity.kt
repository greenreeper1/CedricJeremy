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

    companion object {
        const val TASK_KEY = "task"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskToEdit = intent.getSerializableExtra(TASK_KEY) as Task?
        setContent {
            TaskDetailScreen(taskToEdit, onValidate = {newTask ->
            intent.putExtra(TASK_KEY, newTask)
            setResult(RESULT_OK, intent)
            finish() })

        }
    }
}

@Composable
fun TaskDetailScreen(initialTask: Task?, onValidate: (Task) -> Unit) {
    // Contenu principal de l'écran
    val basicTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
    var title by remember { mutableStateOf(initialTask?.title ?: basicTask.title) }
    var desc by remember { mutableStateOf(initialTask?.description ?: basicTask.description) }
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
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
                val newTask = initialTask?.copy(
                title = title,
                description = desc
            ) ?: Task(
                id = UUID.randomUUID().toString(),
                title = title,
                description = desc
            )
            onValidate(newTask)
        }) {
            Text(text = "Validate")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TaskDetailScreen(initialTask = Task(id = "1", title = "Preview Task", description = "A preview description"), onValidate = {})
}
