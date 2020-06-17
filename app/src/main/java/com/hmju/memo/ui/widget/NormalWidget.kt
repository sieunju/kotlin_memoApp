package com.hmju.memo.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hmju.memo.R
import com.hmju.memo.utils.JLogger

/**
 * Implementation of App Widget functionality.
 */
class NormalWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            JLogger.d("onUpdate WidgetId\t$appWidgetId")
            updateAppWidgetNormal(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        JLogger.d("================[s] onEnabled================")
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context,javaClass))
        for(widgetId in ids){
            JLogger.d("onEnabled Id\t$widgetId")
        }
        JLogger.d("================[e] onEnabled================")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        JLogger.d("================[s] onDisabled================")
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context,javaClass))
        for(widgetId in ids){
            JLogger.d("onDisabled Id\t$widgetId")
        }
        JLogger.d("================[e] onDisabled================")
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        JLogger.d("================[s] onRestored================")
        oldWidgetIds?.forEach { id ->
            JLogger.d("OldWidget Id\t$id")
        }
        newWidgetIds?.forEach { id->
            JLogger.d("NewWidget Id\t$id")
        }
        JLogger.d("================[e] onRestored================")
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        JLogger.d("================[s] onDeleted================")
        appWidgetIds?.forEach { id ->
            JLogger.d("Widget Id\t$id")
        }
        JLogger.d("================[e] onDeleted================")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        JLogger.d("================[s] onReceive================")
        JLogger.d("Intent ${intent.toString()}")
        JLogger.d("================[e] onReceive================")
    }
}

internal fun updateAppWidgetNormal(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.normal_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}