package com.hmju.memo.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hmju.memo.R
import com.hmju.memo.ui.memo.MainActivity
import com.hmju.memo.utils.JLogger
import org.koin.android.ext.android.inject
import java.net.URLDecoder


/**
 * Description : Firebase Service
 *
 * Created by juhongmin on 2020/11/20
 */
class MyFirebaseService : FirebaseMessagingService() {

    private val fcmProvider: FCMProvider by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        try {
            val title = if (remoteMsg.data[KEY_TITLE] == null) DEFAULT_TITLE else URLDecoder.decode(
                remoteMsg.data[KEY_TITLE],
                ENC
            )
            val message = if (remoteMsg.data[KEY_MSG] == null) DEFAULT_MSG else URLDecoder.decode(
                remoteMsg.data[KEY_MSG],
                ENC
            )
            val imgUrl = if (remoteMsg.data[KEY_IMG_URL] == null) null else URLDecoder.decode(
                remoteMsg.data[KEY_IMG_URL],
                ENC
            )
            val linkUrl = if (remoteMsg.data[KEY_LINK_URL] == null) null else URLDecoder.decode(
                remoteMsg.data[KEY_LINK_URL],
                ENC
            )

            val pushId = (System.currentTimeMillis() % Integer.MAX_VALUE).toInt()
            val builder = NotificationCompat.Builder(
                this,
                getString(R.string.notification_channel_id)
            ).apply {
                priority = NotificationManager.IMPORTANCE_HIGH

                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle(title)
                setContentText(message)
                setAutoCancel(true)

                if (!linkUrl.isNullOrEmpty()) {
                    val intent = Intent(this@MyFirebaseService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("linkUrl", linkUrl)
                    }
                    val pendingIntent =
                        PendingIntent.getActivity(
                            this@MyFirebaseService,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                }
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.notify(pushId, builder.build())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {
        const val ENC = "UTF-8"
        const val KEY_TITLE = "title"
        const val DEFAULT_TITLE = "Push Title"
        const val KEY_MSG = "message"
        const val DEFAULT_MSG = "Push Message"
        const val KEY_IMG_URL = "imgUrl"
        const val KEY_LINK_URL = "linkUrl"

    }
}