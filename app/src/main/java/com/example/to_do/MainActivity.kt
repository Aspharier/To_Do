package com.example.to_do

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.to_do.data.TaskDatabase
import com.example.to_do.data.TaskRepository
import com.example.to_do.ui.theme.To_DoTheme
import com.example.to_do.view.TaskViewModel
import com.example.to_do.view.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    // 1. Declare ViewModel and its factory
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskViewModelFactory: TaskViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Initialize the database and repository
        val taskDao = TaskDatabase.getDatabase(applicationContext).taskDao()
        val repository = TaskRepository(taskDao)

        // 3. Initialize the ViewModel factory and the ViewModel
        taskViewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)

        setContent {
            To_DoTheme {
                // 4. Provide the ViewModel to the MainScreen
                MainScreen(viewModel = taskViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    To_DoTheme {
        Greeting("Android")
    }
}