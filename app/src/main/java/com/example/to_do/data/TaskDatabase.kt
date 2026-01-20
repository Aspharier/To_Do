package com.example.to_do.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// This is the main access point to the database. It should be a singleton to prevent multiple instances
// of the database begin open at the same time.

@Database(entities = [Task::class, Habit::class, HabitCompletion::class], version = 2, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                .fallbackToDestructiveMigration() // For simplicity in this exercise, we destroy the old database. In a real app, use migrations.
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
