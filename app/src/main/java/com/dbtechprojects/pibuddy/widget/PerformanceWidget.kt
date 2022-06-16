package com.dbtechprojects.pibuddy.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.dbtechprojects.pibuddy.R

class PerformanceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each widget that belongs to this
        // provider.
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity.
//            val pendingIntent: PendingIntent = PendingIntent.getActivity(
//                /* context = */ context,
//                /* requestCode = */  0,
//                /* intent = */ Intent(context, MainActivity::class.java),
//                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )

            // Get the layout for the widget and attach an on-click listener
            // to the button.
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.performance_widget
            ).apply {
                this.setTextViewText(R.id.widget_cpu, "CPU\n62%")
                this.setTextViewText(R.id.widget_mem, "MEM\n62%")
                this.setTextViewText(R.id.widget_disk, "HDD\n62%")
            }



            // Tell the AppWidgetManager to perform an update on the current
            // widget.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}