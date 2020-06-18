package com.hmju.memo.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.RemoteViews
import androidx.annotation.NonNull
import com.hmju.memo.R
import com.hmju.memo.location.LocationManager
import com.hmju.memo.utils.AppWidgetAlarmManager
import com.hmju.memo.utils.JLogger
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Implementation of App Widget functionality.
 */
class LocationWidget : AppWidgetProvider(), KoinComponent {

    companion object {
        const val ACTION_AUTO_UPDATE = "AUTO_UPDATE"

        /**
         * Widget 표시 업데이트
         */
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            lastLocation: Location?
        ) {

            // 위도 Binding 처리
            lastLocation?.let {
                JLogger.d("===========================[s] updateAppWidget=============================")
                JLogger.d("\n위도\t${it.latitude}\n경도\t${it.longitude}\n정확도\t${it.accuracy}\n위젯 아이디\t$appWidgetId")
                JLogger.d("===========================[n] updateAppWidget=============================")

                // Construct the RemoteViews object
                val views = RemoteViews(context.packageName, R.layout.location_widget)
                views.setTextViewText(R.id.tv_title, "위경도 동기화중..")
                views.setTextViewText(R.id.tv_latitude, "위도 ${it.latitude}")
                views.setTextViewText(R.id.tv_longitude, "경도 ${it.longitude}")
                views.setTextViewText(R.id.tv_accuracy, "정확도 ${it.accuracy}")
                val alarmIntent = Intent(context, LocationWidget::class.java).let { intent ->
                    intent.action = LocationWidget.ACTION_AUTO_UPDATE
                    PendingIntent.getBroadcast(context, AppWidgetAlarmManager.ALARM_ID, intent, 0)
                }

                views.setOnClickPendingIntent(
                    R.id.tv_notify,
                    alarmIntent
                )

                // Binding 처리..
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.tv_title)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tv_latitude)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tv_longitude)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tv_accuracy)

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } ?: run {
                // Construct the RemoteViews object
                val views = RemoteViews(context.packageName, R.layout.location_widget)
                views.setTextViewText(R.id.tv_title, "갱신이 필요합니다..")

                val alarmIntent = Intent(context, LocationWidget::class.java).let { intent ->
                    intent.action = ACTION_AUTO_UPDATE
                    PendingIntent.getBroadcast(context, AppWidgetAlarmManager.ALARM_ID, intent, 0)
                }
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.tv_title)

                views.setOnClickPendingIntent(
                    R.id.tv_notify,
                    alarmIntent
                )

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val locationManager: LocationManager by inject()

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
                lastLocation = locationManager.getLastLocation()
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        // 활성화가 되는 순간 LocationManager 세팅
        JLogger.d("=======================onEnabled=======================")
        // start Alarm
        val appWidgetAlarm = AppWidgetAlarmManager(context.applicationContext)
        appWidgetAlarm.startAlarm()
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        // 이때는 지워야함.
        JLogger.d("=======================onEnabled=======================")
        val appWidgetManager = AppWidgetManager.getInstance(context)

        if (appWidgetManager.getAppWidgetIds(ComponentName(context, javaClass)).isEmpty()) {
            JLogger.d("알림 메니저를 종료합니다.")
            val appWidgetAlarm = AppWidgetAlarmManager(context.applicationContext)
            appWidgetAlarm.stopAlarm()
        }

    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onReceive(@NonNull context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        JLogger.d("=======================onReceive=======================")
        intent?.let {
            if (it.action == ACTION_AUTO_UPDATE) {
                JLogger.d("======================[s] ACTION_AUTO_UPDATE=======================")
                try {
                    val locationManager: LocationManager by inject()
                    val widgetManager = AppWidgetManager.getInstance(context)
                    val ids = widgetManager.getAppWidgetIds(ComponentName(context, javaClass))
                    for (widgetId in ids) {
                        updateAppWidget(
                            context = context,
                            appWidgetManager = widgetManager,
                            appWidgetId = widgetId,
                            lastLocation = locationManager.getLastLocation()
                        )
                    }
                } catch (ex: Exception) {
                    JLogger.d("Error\t${ex.message}")
                } finally {
//                    AppWidgetAlarmManager(context!!.applicationContext).startAlarm()
                    JLogger.d("======================[e] ACTION_AUTO_UPDATE=======================")
                }

            }
        }

    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }
}