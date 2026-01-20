package com.example.to_do.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.to_do.data.Habit
import com.example.to_do.data.HabitCompletion
import com.example.to_do.data.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalStreaks: StateFlow<Int> = repository.allCompletions.map { completions ->
        calculateTotalStreaks(completions)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private fun calculateTotalStreaks(completions: List<HabitCompletion>): Int {
        if (completions.isEmpty()) return 0
        
        val dates = completions.map { LocalDate.parse(it.date) }.distinct().sortedDescending()
        if (dates.isEmpty()) return 0

        var currentStreak = 0
        var checkDate = LocalDate.now()
        
        // If no completion today, check if there was one yesterday to continue the streak
        if (!dates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }

        while (dates.contains(checkDate)) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        }
        
        return currentStreak
    }

    fun addHabit(name: String) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> {
        return repository.getCompletionsForHabit(habitId)
    }

    fun toggleHabitCompletion(habitId: Long, date: LocalDate, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, date.toString(), isCompleted)
        }
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
