package com.cedricjeremy.todo.data

import com.cedricjeremy.todo.list.Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TasksWebService {
    @GET("/rest/v2/tasks/")
    suspend fun fetchTasks(): Response<List<Task>>

    @POST("/rest/v2/tasks/")
    suspend fun create(@Body task: Task): Response<Task>

    @POST("/rest/v2/tasks/{id}")
    suspend fun update(@Body task: Task, @Path("id") id: String = task.id): Response<Task>

// Complétez avec les méthodes précédentes, la doc de l'API, et celle de Retrofit:
    @DELETE
    suspend fun delete(@Body id: String): Response<Unit>
}