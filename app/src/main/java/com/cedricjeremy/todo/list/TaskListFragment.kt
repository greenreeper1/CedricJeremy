package com.cedricjeremy.todo.list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.cedricjeremy.todo.R
import com.cedricjeremy.todo.databinding.FragmentTaskListBinding
import com.cedricjeremy.todo.detail.DetailActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList


class TaskListFragment : Fragment() {
    private var taskList = listOf(
    Task(id = "id_1", title = "Task 1", description = "description 1"),
    Task(id = "id_2", title = "Task 2"),
    Task(id = "id_3", title = "Task 3")
    )

    override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)
        savedState.putSerializable("TASK_LIST", ArrayList(taskList))
    }

    private val adapterListener = object : TaskListListener{
        override fun onClickDelete(task: Task) {
            taskList = taskList.filter { it.id != task.id }
            refreshAdapter()
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(TASK_KEY, task)
            detailLauncher.launch(intent)
        }

        override fun shareText(activity: Activity, textToShare: String) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, textToShare)
            }
            activity.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }
    companion object {
        const val TASK_KEY = "task"
    }

    // Déclaration du launcher
    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        /*val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
        taskList = taskList + newTask*/
        val task = result.data?.getSerializableExtra(TASK_KEY) as Task?
        if (task != null){
            taskList = taskList + task
        }
        refreshAdapter()
    }

    private val detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Récupère la tâche mise à jour depuis le résultat
            val updatedTask = result.data?.getSerializableExtra(DetailActivity.TASK_KEY) as? Task
            updatedTask?.let {
                // Mets à jour l'adaptateur ici
                taskList = taskList.map { if (it.id == updatedTask.id) updatedTask else it }
                refreshAdapter()
            }
        }
    }

    private val adapter = TaskListAdapter(adapterListener)

    private lateinit var binding : FragmentTaskListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(layoutInflater)
        val rootView = binding.root
        taskList = if (savedInstanceState != null) {
            savedInstanceState.getSerializable("TASK_LIST") as? List<Task> ?: emptyList()
        } else {
            taskList
        }
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
            createTask.launch(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter(){
        adapter.submitList(taskList)
        adapter.notifyDataSetChanged()
    }
}

