package com.example.to_do.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data class represents a table in the room database. Each instance of this class will be a row in the "tasks"

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
