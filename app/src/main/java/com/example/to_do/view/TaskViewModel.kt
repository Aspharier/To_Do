package com.example.to_do.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.to_do.data.Task
import com.example.to_do.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// The TaskViewModel will manage the UI state and handle user interactions by calling the appropriate
// methods in the repository. It exposes a StateFlow of the task list that UI can observe.

class TaskViewModel(private val repository: TaskRepository): ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            repository.allTasks.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(description: String) {
        if(description.isNotBlank()) {
            viewModelScope.launch {
                repository.insert(Task(description = description))
            }
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}


// Factory to provide the repository to the ViewModel.
class TaskViewModelFactory(private val repository: TaskRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}