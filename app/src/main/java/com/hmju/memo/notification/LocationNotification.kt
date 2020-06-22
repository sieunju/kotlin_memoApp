package com.hmju.memo.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.hmju.memo.R
import com.hmju.memo.location.LocationManager
import com.hmju.memo.ui.notification.NotificationResultActivity
import com.hmju.memo.utils.JLogger
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Description :
 *
 * Created by hmju on 2020-06-22
 */
class LocationNotification : Service(), KoinComponent {


    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, NotificationResultActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val remoteViews = RemoteViews(packageName, R.layout.notification_location)
        val locationManager: LocationManager by inject()

        locationManager.getLastLocation()?.let {
            remoteViews.setTextViewText(R.id.tv_latitude, "위도 ${it.latitude}")
            remoteViews.setTextViewText(R.id.tv_longitude, "경도 ${it.longitude}")
            remoteViews.setTextViewText(R.id.tv_accuracy, "정확도 ${it.accuracy}")
        } ?: run {
            remoteViews.setTextViewText(R.id.tv_title, "GPS 정보 수집중..")
        }

        val builder =
            NotificationCompat.Builder(this, getString(R.string.notification_channel_group)).apply {
                setSmallIcon(R.mipmap.ic_launcher)
                setContent(remoteViews)
                setContentIntent(pendingIntent)
            }

        startForeground(1001, builder.build())

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
                JLogger.d("onLocation\n정확도\t$accuracy\n위도\t$latitude\n경도\t$longitude")
                remoteViews.setTextViewText(R.id.tv_latitude, "위도 $latitude")
                remoteViews.setTextViewText(R.id.tv_longitude, "경도 $longitude")
                remoteViews.setTextViewText(R.id.tv_accuracy, "정확도 $accuracy")
                startForeground(1001, builder.build())
            }
        })
        locationManager.start(null)
    }

    override fun onDestroy() {
        JLogger.d("stopService !!!!!!")
        val locationManager : LocationManager by inject()
        locationManager.stopLocation()
        super.onDestroy()
    }
}