package com.example.to_do.data

import kotlinx.coroutines.flow.Flow

// The repository abstracts the data source. For this app, it will simply call the DAO methods, but in more
// complex app, it could handle fetching data from a remote server or managing multiple data sources.

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}