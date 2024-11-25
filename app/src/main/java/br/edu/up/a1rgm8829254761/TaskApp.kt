package br.edu.up.a1rgm8829254761

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.up.a1rgm8829254761.model.Task
import br.edu.up.a1rgm8829254761.model.TaskViewModel

@Composable
fun TaskApp(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TaskList(
                    tasks = tasks,
                    onEdit = { task ->
                        taskToEdit = task
                        showDialog = true
                    },
                    onDelete = { task ->
                        viewModel.deleteTask(task)
                    }
                )
                if (showDialog) {
                    AddTaskDialog(
                        task = taskToEdit,
                        onDismiss = {
                            showDialog = false
                            taskToEdit = null
                        },
                        onAdd = { title, description ->
                            if (taskToEdit != null) {
                                viewModel.updateTask(taskToEdit!!.copy(title = title, description = description))
                            } else {
                                viewModel.addTask(title, description)
                            }
                            showDialog = false
                            taskToEdit = null
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AddTaskDialog(task: Task? = null, onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (task == null) "Add Task" else "Edit Task") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, description) }
            ) {
                Text(if (task == null) "Add" else "Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskList(tasks: List<Task>, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        tasks.forEach { task ->
            TaskItem(task, onEdit, onDelete)
            Divider()
        }
    }
}


@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(task.title, style = MaterialTheme.typography.titleLarge)
            Text(task.description, style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            IconButton(onClick = { onEdit(task) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Task")
            }
            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

