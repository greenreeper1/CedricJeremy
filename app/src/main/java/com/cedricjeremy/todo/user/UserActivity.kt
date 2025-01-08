package com.cedricjeremy.todo.user

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import coil3.Bitmap
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


class UserActivity : ComponentActivity() {

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val userViewModel: UserViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val avatarUri by userViewModel.avatarUri.collectAsState()

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

            val uploadPicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    val avatar = it.toRequestBody()
                    userViewModel.updateAvatar(avatar)
                }
            }

            val askPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = avatarUri ?: captureUri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        captureUri?.let { uri -> takePicture.launch(uri) }
                    },
                    content = { Text("Take high-quality picture") }
                )
                Button(
                    onClick = {
                        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        uploadPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    content = { Text("Pick photo") }
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
