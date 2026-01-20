package com.example.to_do

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Todo : BottomNavItem("Tasks", Icons.Default.List, "todo")
    object Habits : BottomNavItem("Habits", Icons.Default.DateRange, "habits")
}
