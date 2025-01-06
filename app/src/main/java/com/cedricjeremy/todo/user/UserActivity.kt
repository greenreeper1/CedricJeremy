package com.cedricjeremy.todo.user

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }
            val composeScope = rememberCoroutineScope()
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it
                val avatar = it!!.toRequestBody()
                composeScope.launch {
                    Api.userWebService.updateAvatar(avatar)
                }
            }
            val uploadPicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri->
                val avatar = uri!!.toRequestBody()
                composeScope.launch {
                    Api.userWebService.updateAvatar(avatar)
                }
            }
            val askPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {

            }
            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        takePicture.launch()
                    },
                    content = { Text("Take picture") }
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

