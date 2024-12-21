package com.cedricjeremy.todo.list

import android.app.Activity

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)

    fun shareText(activity: Activity, textToShare: String)
}