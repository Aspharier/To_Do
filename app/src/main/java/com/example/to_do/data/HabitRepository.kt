package com.example.to_do.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletions()

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteCompletionsForHabit(habit.id)
        habitDao.deleteHabit(habit)
    }

    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> {
        return habitDao.getCompletionsForHabit(habitId)
    }

    suspend fun toggleCompletion(habitId: Long, date: String, isCompleted: Boolean) {
        if (isCompleted) {
            habitDao.insertCompletion(HabitCompletion(habitId, date))
        } else {
            habitDao.deleteCompletion(HabitCompletion(habitId, date))
        }
    }
}
