package com.hmju.memo.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.hmju.memo.ui.widget.LocationWidget
import java.util.*

/**
 * Description : 위젯 전용 알림 메니저
 *
 * Created by hmju on 2020-06-18
 */
class AppWidgetAlarmManager(private val context: Context?) {
    companion object {
        const val ALARM_ID = 0
        const val INTERVAL_MILLIS: Long = 1000 // 1초
    }

    fun startAlarm() {
        JLogger.d("================startAlarm================")

        val alarmIntent = Intent(context, LocationWidget::class.java).let { intent ->
            intent.action = LocationWidget.ACTION_AUTO_UPDATE
            PendingIntent.getBroadcast(context, ALARM_ID, intent, 0)
        }
        with(context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS.toInt())
            setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL_MILLIS,
                INTERVAL_MILLIS, alarmIntent
            )
        }
    }

    fun stopAlarm() {
        JLogger.d("================stopAlarm================")
        val alarmIntent = Intent(LocationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}