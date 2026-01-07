package com.example.to_do.todoWidget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.to_do.R
import com.example.to_do.data.Task

class TodoWidgetFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {
    private var tasks: List<Task> = emptyList()
    override fun onCreate() {}

    override fun onDataSetChanged() {
        tasks = TaskRepositoryProvider
            .getRepository(context)
            .getTaskForWidget()
    }

    override fun getCount() = tasks.size

    override fun getViewAt(position: Int) : RemoteViews {
        val task = tasks[position]
        val views = RemoteViews(
            context.packageName,
            R.layout.widget_task_item
        )
        views.setTextViewText(R.id.widget_task_text, task.description)
        views.setBoolean(
            R.id.widget_checkbox,
            "setChecked",
            task.isCompleted
        )

        val intent = Intent(context, TodoAppWidget::class.java).apply {
            action = ACTION_TOGGLE_TASK
            putExtra(EXTRA_TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_checkbox, pendingIntent)

        return views
    }

    override fun getItemId(position: Int) : Long = tasks[position].id.toLong()
    override fun getLoadingView() = null
    override fun hasStableIds() = true
    override fun getViewTypeCount() = 1
    override fun onDestroy() {}
}