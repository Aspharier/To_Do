package com.example.to_do.todoWidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.activity.result.launch
import com.example.to_do.MainActivity
import com.example.to_do.R
import com.example.to_do.data.Task
import com.example.to_do.data.TaskDatabase
import com.example.to_do.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [TodoAppWidgetConfigureActivity]
 */
class TodoAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(
            appWidgetIds,
            R.id.widget_list_view
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DELETE_TASK) {
            val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)

            if (taskId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val taskDao = TaskDatabase.getDatabase(context).taskDao()

                    val taskToDelete = Task(id = taskId, description = "", isCompleted = false)
                    taskDao.deleteTask(taskToDelete)

                    // After deleting, notify the widget to update its list
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, TodoAppWidget::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view)
                }
            }
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_DELETE_TASK = "com.example.to_do.ACTION_DELETE_TASK"
        const val ACTION_COMPLETE_TASK = "com.example.to_do.ACTION_COMPLETE_TASK"
        const val EXTRA_TASK_ID = "com.example.to_do.EXTRA_TASK_ID"
        const val EXTRA_IS_COMPLETED = "com.example.to_do.EXTRA_IS_COMPLETED"

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.todo_app_widget
            )

            // Intent to launch the main app when the header is clicked
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(
                R.id.widget_header,
                openAppPendingIntent
            )
//            views.setOnClickPendingIntent(
//                R.id.widget_open_app_button,
//                openAppPendingIntent
//            )
            // Set up the RemoteViews service to populate the list
            val serviceIntent = Intent(context, TodoWidgetService::class.java)
            views.setRemoteAdapter(R.id.widget_list_view, serviceIntent)

            // Set up the template for click events on list items
            val deleteTaskIntent = Intent(context, TodoAppWidget::class.java).apply {
                action = ACTION_DELETE_TASK
            }
            val deleteTaskPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                deleteTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(
                R.id.widget_list_view,
                deleteTaskPendingIntent
            )

            // Show empty view when the list is empty
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
