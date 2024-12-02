package com.cedricjeremy.todo.list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cedricjeremy.todo.R
import com.cedricjeremy.todo.databinding.FragmentTaskListBinding
import com.cedricjeremy.todo.detail.DetailActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class TaskListFragment : Fragment() {
    private var taskList = listOf(
    Task(id = "id_1", title = "Task 1", description = "description 1"),
    Task(id = "id_2", title = "Task 2"),
    Task(id = "id_3", title = "Task 3")
    )
    private val adapter = TaskListAdapter()

    // Déclaration du launcher
    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
    }

    private lateinit var binding : FragmentTaskListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(layoutInflater)
        val rootView = binding.root
        adapter.submitList(taskList)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.recycler
        recyclerView.adapter = adapter

        val addButton = view.findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener()
        {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivity(intent)
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

