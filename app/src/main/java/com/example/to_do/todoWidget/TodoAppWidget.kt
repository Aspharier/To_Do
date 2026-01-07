package com.example.to_do.todoWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.to_do.R
import com.example.to_do.data.TaskRepository

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
        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(
                context.packageName,
                R.layout.todo_app_widget
            )

            val intent = Intent(context, TodoWidgetService::class.java)
            views.setRemoteAdapter(R.id.widget_task_list, intent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(context == null || intent == null) return

        if(intent.action == ACTION_TOGGLE_TASK) {
            val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
            if(taskId == -1L) return

            TaskRepositoryProvider
                .getRepository(context)
                .toggleTaskCompleted(taskId)

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, TodoAppWidget::class.java)
            )
            manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_task_list)
        }
    }
}
