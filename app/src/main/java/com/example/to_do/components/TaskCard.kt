package com.example.to_do.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import com.example.to_do.data.Task
import kotlinx.coroutines.delay


// The TaskCard will display a single task. It will use the SwipeToDismissBox composable to handle the
// Swipe-to-complete gesture.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(true) }
    var isCompleted by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when(value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    isCompleted = !isCompleted
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    visible = false
                    true
                }
                else -> false
            }
        }
    )

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(tween(200)) + slideOutVertically(
            targetOffsetY = { -it / 2 },
            animationSpec = tween(200)
        ),
        enter = fadeIn(tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            backgroundContent = {
//            val color = when (dismissState.targetValue) {
//                SwipeToDismissBoxValue.EndToStart -> Color.Green.copy(alpha = 0.5f)
//                else -> Color.Transparent
//            }
                val icon: ImageVector?
                val alignment: Alignment
                val bgColor: Color
                val iconColor: Color

                when(dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        icon = Icons.Default.Check
                        alignment = Alignment.CenterStart
                        bgColor = MaterialTheme.colorScheme.primaryContainer
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        icon = Icons.Default.Delete
                        alignment = Alignment.CenterEnd
                        bgColor = MaterialTheme.colorScheme.errorContainer
                        iconColor = MaterialTheme.colorScheme.onErrorContainer
                    }

                    else -> {
                        icon = null
                        alignment = Alignment.Center
                        bgColor = Color.Transparent
                        iconColor = Color.Transparent
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = alignment
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = iconColor
                        )
                    }
                }
            }
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = task.description,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if(isCompleted){
                            TextDecoration.LineThrough
                        } else TextDecoration.None,
                        color = if(isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }

    LaunchedEffect(visible) {
        if(!visible) {
            delay(200)
            onComplete()
        }
    }
}