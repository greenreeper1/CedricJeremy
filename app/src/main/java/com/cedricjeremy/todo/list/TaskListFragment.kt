package com.cedricjeremy.todo.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cedricjeremy.todo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class TaskListFragment : Fragment() {
    private var taskList = listOf(
    Task(id = "id_1", title = "Task 1", description = "description 1"),
    Task(id = "id_2", title = "Task 2"),
    Task(id = "id_3", title = "Task 3")
    )
    private val adapter = TaskListAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        adapter.submitList(taskList)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = adapter

        val addButton = view.findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener(){
            // Instanciation d'un objet task avec des données préremplies:
            val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            taskList = taskList + newTask
            refreshAdapter()
            }

        adapter.onClickDelete = { task ->
            taskList = taskList.filter { it.id != task.id }
            refreshAdapter()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter(){
        adapter.submitList(taskList)
        adapter.notifyDataSetChanged()
    }
}

