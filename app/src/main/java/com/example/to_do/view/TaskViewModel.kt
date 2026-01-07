package com.example.to_do.view

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.to_do.data.Task
import com.example.to_do.data.TaskRepository
import com.example.to_do.todoWidget.TodoAppWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// The TaskViewModel will manage the UI state and handle user interactions by calling the appropriate
// methods in the repository. It exposes a StateFlow of the task list that UI can observe.

class TaskViewModel(
    application: Application,
    private val repository: TaskRepository
): AndroidViewModel(application) {

    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks ->
            tasks.sortedBy { it.isCompleted }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val incompleteTasks: StateFlow<List<Task>> = repository.allTasks
        .map { tasks -> tasks.filter { !it.isCompleted } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun addTask(description: String) {
        if(description.isNotBlank()) {
            viewModelScope.launch {
                repository.insert(Task(description = description,))
                updateAllWidgets()
            }
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = true)
            repository.update(updatedTask)
            updateAllWidgets()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            updateAllWidgets()
        }
    }

    private fun updateAllWidgets() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(
            context,
            TodoAppWidget::class.java
        ).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(
            context,
            com.example.to_do.todoWidget.TodoAppWidget::class.java
        )
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        intent.putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_IDS,
            appWidgetIds
        )
        context.sendBroadcast(intent)
    }
}


// Factory to provide the repository to the ViewModel.
class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}