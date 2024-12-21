package com.cedricjeremy.todo.list

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}