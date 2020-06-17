package com.hmju.memo.ui.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hmju.memo.R
import com.hmju.memo.location.LocationManager
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

/**
 * Implementation of App Widget functionality.
 */
class MemoAppWidget : AppWidgetProvider(), KoinComponent {

    private val actPref: AccountPref by inject()
    private val locationManager: LocationManager by inject()
    companion object{
        private var pendingIntent : PendingIntent? = null
        private  var alarmManager: AlarmManager? = null
        private var mAccuracy = 0F
        private var mLatitude = 0.0
        private var mLongitude = 0.0
        private var widgetId = 0
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            JLogger.d("onUpdate WidgetId\t$appWidgetId")
            updateAppWidget(
                appWidgetManager,
                appWidgetId
            )
        }
    }

    fun updateAppWidget(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
//        widgetId = appWidgetId
        val views = RemoteViews((get() as Context).packageName, R.layout.memo_app_widget)
        views.setTextViewText(R.id.tv_title, "위경도 동기화중...")
        views.setTextViewText(R.id.tv_latitude, "위도\t$mLatitude")
        views.setTextViewText(R.id.tv_longitude, "경도\t$mLongitude")
        views.setTextViewText(R.id.tv_accuracy, "정확도\t$mAccuracy%")
        JLogger.d("updateAppWidget WidgetId $appWidgetId")
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        JLogger.d("================[s] onEnabled================")
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        for (widgetId in ids) {
            JLogger.d("onEnabled Id\t$widgetId")
        }
        JLogger.d("================[e] onEnabled================")
        locationManager.start(null)
        locationManager.setListener(object : LocationManager.Listener {
            override fun onPermissionDenied() {
                JLogger.d("onPermissionDenied")
            }

            override fun onGpsAndNetworkError(isGpsError: Boolean) {
                if (isGpsError) {
                    JLogger.d("onGpsAndNetworkError GPS")
                } else {
                    JLogger.d("onGpsAndNetworkError Network")
                }
            }

            override fun onLocation(
                provider: String,
                accuracy: Float,
                latitude: Double,
                longitude: Double
            ) {
                JLogger.d("==================[s] Location Changed===================")
                JLogger.d("정확도\t$accuracy\n위도\t$latitude\n경도$longitude")
                JLogger.d("==================[e] Location Changed===================")
                mAccuracy = accuracy
                mLatitude = latitude
                mLongitude = longitude

//                val views = RemoteViews((get() as Context).packageName, R.layout.memo_app_widget)
//                views.setTextViewText(R.id.tv_title, "위경도 동기화중...")
//                views.setTextViewText(R.id.tv_latitude, "위도\t$latitude")
//                views.setTextViewText(R.id.tv_longitude, "경도\t$longitude")
//                views.setTextViewText(R.id.tv_accuracy, "정확도\t$accuracy")
//                AppWidgetManager.getInstance(get() as Context).updateAppWidget(widgetId, views)
            }
        })
    }

    /**
     * 위젯이 다 없어지는 경우
     */
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        JLogger.d("================[s] onDisabled================")
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        for (widgetId in ids) {
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
        newWidgetIds?.forEach { id ->
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

        intent?.action?.let{
            JLogger.d("TEST:: 고고고고고고고")
            val nextTime = System.currentTimeMillis() + 5000
            pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager!!.set(AlarmManager.RTC,nextTime, pendingIntent)
        }
    }


}