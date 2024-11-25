package br.edu.up.a1rgm8829254761.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val db = FirebaseFirestore.getInstance()

    init {
        viewModelScope.launch {
            db.collection("tasks")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    val taskList = mutableListOf<Task>()
                    for (document in snapshot!!.documents) {
                        val task = document.toObject(Task::class.java)
                        task?.let { taskList.add(it) }
                    }
                    _tasks.value = taskList
                }
        }
    }

    fun addTask(title: String, description: String) {
        val newTask = Task(id = db.collection("tasks").document().id, title = title, description = description)
        db.collection("tasks").document(newTask.id).set(newTask)
    }

    fun updateTask(task: Task) {
        db.collection("tasks").document(task.id).set(task)
    }

    fun deleteTask(task: Task) {
        db.collection("tasks").document(task.id).delete()
    }
}
