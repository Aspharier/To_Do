package com.example.to_do

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.to_do.data.HabitRepository
import com.example.to_do.data.TaskDatabase
import com.example.to_do.data.TaskRepository
import com.example.to_do.ui.theme.ToDoTheme
import com.example.to_do.view.HabitViewModel
import com.example.to_do.view.HabitViewModelFactory
import com.example.to_do.view.TaskViewModel
import com.example.to_do.view.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var habitViewModel: HabitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TaskDatabase.getDatabase(application)
        
        val taskRepository = TaskRepository(database.taskDao())
        val taskViewModelFactory = TaskViewModelFactory(taskRepository, application)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory)[TaskViewModel::class.java]

        val habitRepository = HabitRepository(database.habitDao())
        val habitViewModelFactory = HabitViewModelFactory(habitRepository)
        habitViewModel = ViewModelProvider(this, habitViewModelFactory)[HabitViewModel::class.java]

        setContent {
            ToDoTheme {
                MainScreen(
                    taskViewModel = taskViewModel,
                    habitViewModel = habitViewModel
                )
            }
        }
    }
}
