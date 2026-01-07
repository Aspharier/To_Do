package com.example.to_do

import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
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
import com.example.to_do.components.AnimatedTaskItem
import com.example.to_do.components.TaskCard
import com.example.to_do.data.Task
import com.example.to_do.view.TaskViewModel

// This is the main entry point for the UI. It will display the list of tasks or on empty state message and
// include the Floating Action Button (FAB).

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
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
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
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
fun TaskList(
    tasks: List<Task>,
    onCompleteTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            AnimatedTaskItem(
                task = task,
                onCompleteTask = { onCompleteTask(task) },
                onDeleteTask = { onDeleteTask(task) }
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No tasks yet",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Tap + to add your first task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
