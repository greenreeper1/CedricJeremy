package com.cedricjeremy.todo.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedricjeremy.todo.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserViewModel : ViewModel() {

    private val _avatarUri = MutableStateFlow<String?>(null)
    val avatarUri = _avatarUri.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    fun updateAvatar(avatar: MultipartBody.Part) {
        viewModelScope.launch {
            val response = Api.userWebService.updateAvatar(avatar)
            if (response.isSuccessful) {
                _avatarUri.value = response.body()?.avatar
            } else {
                // Handle error (e.g., log or show a message)
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val userId = "12345" // Replace with actual user ID if available
            val userUpdate = UserUpdate(
                commands = listOf(
                    Command(args = UserArgs(id = userId, name = newName))
                )
            )
            val response = Api.userWebService.update(userUpdate)
            if (response.isSuccessful) {
                _userName.value = newName
            } else {
                // Handle error (e.g., log or show a message)
            }
        }
    }
}

