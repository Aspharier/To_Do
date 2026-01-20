package com.example.to_do

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.to_do.data.Habit
import com.example.to_do.data.HabitCompletion
import com.example.to_do.view.HabitViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(viewModel: HabitViewModel) {
    val habits by viewModel.allHabits.collectAsState()
    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Habit Tracker") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddHabitDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (habits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No habits yet. Start one!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(habits) { habit ->
                        HabitCard(habit, viewModel)
                    }
                }
            }
        }

        if (showAddHabitDialog) {
            AddHabitDialog(
                onDismiss = { showAddHabitDialog = false },
                onSave = { name ->
                    viewModel.addHabit(name)
                    showAddHabitDialog = false
                }
            )
        }
    }
}

@Composable
fun HabitCard(habit: Habit, viewModel: HabitViewModel) {
    val completions by viewModel.getCompletionsForHabit(habit.id).collectAsState(initial = emptyList())
    var isExpanded by remember { mutableStateOf(true) }
    val currentMonth = YearMonth.now()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${completions.size}🔥  ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                CalendarGrid(currentMonth, completions) { date, completed ->
                    viewModel.toggleHabitCompletion(habit.id, date, completed)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { viewModel.deleteHabit(habit) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    month: YearMonth,
    completions: List<HabitCompletion>,
    onToggle: (LocalDate, Boolean) -> Unit
) {
    val daysInMonth = month.lengthOfMonth()
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        val adjustedFirstDay = (month.atDay(1).dayOfWeek.value - 1)
        
        var currentDay = 1
        for (i in 0..5) {
            if (currentDay > daysInMonth) break
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0..6) {
                    val isWithinMonth = (i > 0 || j >= adjustedFirstDay) && currentDay <= daysInMonth
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isWithinMonth) {
                            val date = month.atDay(currentDay)
                            val isCompleted = completions.any { it.date == date.toString() }
                            val isToday = date == LocalDate.now()
                            val isWeekend = j == 5 || j == 6

                            HabitDayCell(
                                day = currentDay,
                                isCompleted = isCompleted,
                                isToday = isToday,
                                isWeekend = isWeekend,
                                onClick = { onToggle(date, !isCompleted) }
                            )
                            currentDay++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitDayCell(
    day: Int,
    isCompleted: Boolean,
    isToday: Boolean,
    isWeekend: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            ScribbleIcon(modifier = Modifier.fillMaxSize(0.7f))
        } else {
            Text(
                text = day.toString(),
                color = if (isWeekend) Color(0xFFEF5350) else Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ScribbleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width * 0.1f, height * 0.1f)
            lineTo(width * 0.9f, height * 0.9f)
            moveTo(width * 0.9f, height * 0.1f)
            lineTo(width * 0.1f, height * 0.9f)
            
            moveTo(width * 0.2f, height * 0.5f)
            quadraticTo(width * 0.5f, height * 0.2f, width * 0.8f, height * 0.5f)
            quadraticTo(width * 0.5f, height * 0.8f, width * 0.2f, height * 0.5f)
        }
        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun AddHabitDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Habit") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Habit name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
