package com.cedricjeremy.todo.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(@SerialName("content")
                var title: String,
                @SerialName("description")
                var description: String = "Ceci est une description",
                @SerialName("id")
                var id: String) : java.io.Serializable{

}
