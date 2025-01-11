package com.cedricjeremy.todo.user

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.cedricjeremy.todo.data.Api
import com.cedricjeremy.todo.user.ui.theme.CedricJeremyTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.dp
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.Serializable

class UserActivity : ComponentActivity() {
    private lateinit var uploadPicture: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var askPermission: ActivityResultLauncher<String>
    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }

    private fun pickPhotoWithPermission() {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES //Pour les versions d'Android supérieure à Android 13
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val permissionStatus = checkSelfPermission(storagePermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(storagePermission)

        when {
            isAlreadyAccepted -> {// lancer l'action souhaitée
                uploadPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
                isExplanationNeeded -> {// afficher une explication
                    showMessage("Cette permission est requise pour accéder aux photos")
                    askPermission.launch(storagePermission)
                }
            else -> {// lancer la demande de permission et afficher une explication en cas de refus
                askPermission.launch(storagePermission)
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val userViewModel: UserViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val avatarUri by userViewModel.avatarUri.collectAsState()
            var userName by remember { mutableStateOf("") } // Nom de l'utilisateur
            val captureUri = remember {
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
            }

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    captureUri?.let { uri ->
                        val avatar = uri.toRequestBody()
                        userViewModel.updateAvatar(avatar)
                    }
                }
            }

            uploadPicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    val avatar = it.toRequestBody()
                    userViewModel.updateAvatar(avatar)
                }
            }

            askPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    // Permission accordée, lancer la galerie
                    uploadPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    // Permission refusée, informer l'utilisateur
                    showMessage("Vous devez accorder la permission pour choisir une photo.")
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = avatarUri ?: captureUri,
                    contentDescription = null
                )
                TextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nom d'utilisateur") }
                )
                Button(
                    onClick = {
                        userViewModel.updateUserName(userName)
                    },
                    content = { Text("Enregistrer le nom") }
                )
                Button(
                    onClick = {
                        captureUri?.let { uri -> takePicture.launch(uri) }
                    },
                    content = { Text("Prendre une photo") }
                )
                Button(
                    onClick = {
                        pickPhotoWithPermission()
                    },
                    content = { Text("Choisir une photo") }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CedricJeremyTheme {
        Greeting("Android")
    }
}

private fun Bitmap.toRequestBody(): MultipartBody.Part {
    val tmpFile = File.createTempFile("avatar", "jpg")
    tmpFile.outputStream().use { // *use*: open et close automatiquement
        this.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
    }
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = tmpFile.readBytes().toRequestBody()
    )
}



@Serializable
data class UserUpdate(
    val commands: List<Command>
)

@Serializable
data class Command(
    val type: String = "user_update",
    val args: UserArgs
)

@Serializable
data class UserArgs(
    val id: String,  // L'ID de l'utilisateur
    val name: String // Le nouveau nom d'utilisateur
)
