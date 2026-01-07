package com.example.to_do.todoWidget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.layout
import com.example.to_do.R
import com.example.to_do.data.Task
import com.example.to_do.data.TaskDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoWidgetItemFactory(applicationContext)
    }
}

class TodoWidgetItemFactory(private val context: Context): RemoteViewsService.RemoteViewsFactory {
    private var tasks: List<Task> = emptyList()

    override fun onDataSetChanged() {
        // This is where we fetch the data. It's called on a background thread.
        val taskDao = TaskDatabase.getDatabase(context).taskDao()
        runBlocking { // runBlocking is acceptable here as this is a background thread
            tasks = taskDao.getAllTasks().first().sortedBy { !it.isCompleted }
        }
    }

    override fun getCount(): Int = tasks.size

    override fun getViewAt(position: Int): RemoteViews {
        val task = tasks.getOrNull(position) ?: return RemoteViews(context.packageName, R.layout.widget_task_item)
        val views = RemoteViews(context.packageName, R.layout.widget_task_item)

        views.setTextViewText(R.id.widget_task_description, task.description)

        // FIX: The fill-in intent now only needs the Task ID for deletion.
        val fillInIntent = Intent().apply {
            putExtra(TodoAppWidget.EXTRA_TASK_ID, task.id)
        }
//        views.setOnClickFillInIntent(R.id.widget_complete_button, fillInIntent)

        return views
    }

    override fun onCreate() {}
    override fun onDestroy() {}
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = tasks[position].id.toLong()
    override fun hasStableIds(): Boolean = true
}