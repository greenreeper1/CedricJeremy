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

    fun updateAvatar(avatar: MultipartBody.Part) {
        viewModelScope.launch {
            val response = Api.userWebService.updateAvatar(avatar)
            if (response.isSuccessful) {
                _avatarUri.value = response.body()?.avatarUrl
            }
        }
    }
}
