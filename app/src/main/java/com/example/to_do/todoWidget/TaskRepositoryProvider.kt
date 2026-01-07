package com.example.to_do.todoWidget

import android.content.Context
import com.example.to_do.data.TaskDatabase
import com.example.to_do.data.TaskRepository

object TaskRepositoryProvider {
    @Volatile
    private var repository: TaskRepository? = null

    fun getRepository(context: Context) : TaskRepository {
        return repository ?: synchronized(this) {
            repository ?: run {
                val db = TaskDatabase.getDatabase(context)
                TaskRepository(db.taskDao()).also {
                    repository = it
                }
            }
        }
    }
}