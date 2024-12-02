package com.cedricjeremy.todo.list

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cedricjeremy.todo.R
import com.cedricjeremy.todo.databinding.FragmentTaskListBinding
import com.cedricjeremy.todo.databinding.ItemTaskBinding

object MyItemsDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id == newItem.id // comparaison: est-ce la même "entité" ? => même id?
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.description == newItem.description  // comparaison: est-ce le même "contenu" ? => mêmes valeurs? (avec data class: simple égalité)
    }
}

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(MyItemsDiffCallback) {

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement

    private lateinit var binding : ItemTaskBinding
    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        var textViewTitle = binding.taskTitle
        var textViewDesc = binding.taskDescription
        fun bind(task: Task) {
            // on affichera les données ici
            textViewTitle.text = task.title
            textViewDesc.text = task.description

            deleteButton.setOnClickListener {
                onClickDelete(task) // Appelle la lambda pour gérer la suppression
            }
        }
    }

    var onClickDelete: (Task) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context))
        val taskViewHolder = TaskViewHolder(binding)
        return taskViewHolder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}