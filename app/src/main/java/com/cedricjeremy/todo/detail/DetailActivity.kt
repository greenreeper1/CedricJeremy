package com.cedricjeremy.todo.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cedricjeremy.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {

    companion object {
        const val TASK_KEY = "task"
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer le texte partagé (si présent)
        val sharedText = if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

        val taskToEdit = intent.getSerializableExtra(TASK_KEY) as Task?
        setContent {
            TaskDetailScreen(
                initialTask = taskToEdit,
                sharedDescription = sharedText,
                onValidate = { newTask ->
                    intent.putExtra(TASK_KEY, newTask)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun TaskDetailScreen(
    initialTask: Task?,
    sharedDescription: String? = null,
    onValidate: (Task) -> Unit
) {

    val context = LocalContext.current
    // Déterminer les valeurs initiales
    val basicTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
    var title by remember { mutableStateOf(initialTask?.title ?: basicTask.title) }
    var desc by remember { mutableStateOf(sharedDescription ?: initialTask?.description ?: basicTask.description) }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") }
        )
        Button(onClick = {
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
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de partage
        Button(onClick = {
            val textToShare = "Task: $title\nDescription: $desc"
            shareText(
                context = context,
                textToShare = textToShare
            )
        }) {
            Text(text = "Share Task")
        }
    }
}
fun shareText(context: Context, textToShare: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}
@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TaskDetailScreen(
        initialTask = Task(id = "1", title = "Preview Task", description = "A preview description"),
        onValidate = {}
    )
}