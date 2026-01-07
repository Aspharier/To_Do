package com.example.to_do.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.to_do.data.Task

@Composable
fun AnimatedTaskItem(
    task: Task,
    onCompleteTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(300)
        ) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300)
        ),
        exit = fadeOut(
            animationSpec = tween(200)
        )
    ) {
        TaskCard(
            task = task,
            onComplete = { onCompleteTask(task) },
            onDelete = { onDeleteTask(task) },
            modifier = Modifier.animateContentSize()
        )
    }
}