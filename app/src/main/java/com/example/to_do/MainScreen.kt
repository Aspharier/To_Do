package com.example.to_do

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.to_do.components.AddTaskDialog
import com.example.to_do.components.TaskCard
import com.example.to_do.data.Task
import com.example.to_do.view.TaskViewModel

// This is the main entry point for the UI. It will display the list of tasks or on empty state message and
// include the Floating Action Button (FAB).

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Add Task"
                )
            }
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if(tasks.isEmpty()) {
                EmptyState()
            } else {
                TaskList(
                    tasks = tasks,
                    onCompleteTask = { task ->
                        viewModel.completeTask(task)
                    }
                )
            }
            if(showDialog) {
                AddTaskDialog(
                    onDismiss = { showDialog = false },
                    onSave = { description ->
                        viewModel.addTask(description)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, onCompleteTask: (Task) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onComplete = { onCompleteTask(task) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No tasks yet. Tap + to create one!",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}