package com.hmju.memo.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.hmju.memo.R
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.Single

/**
 * Description : Firebase Messaging Service Provider Class
 *
 * Created by juhongmin on 2020/11/20
 */
interface FCMProvider {
    fun createToken(): Single<String?>
    fun createNotificationChannel()
}

class FCMProviderImpl(private val ctx: Context) : FCMProvider {
    override fun createToken(): Single<String?> = Single.create { callback ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            JLogger.d("createToken $task")
            if (!task.isSuccessful) {
                callback.onError(Throwable(task.exception))
                return@OnCompleteListener
            }

            val token = task.result
            token?.also {
                callback.onSuccess(token)
            }
        })
    }

    override fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = ctx.getString(R.string.notification_channel_id)
            val name = ctx.getString(R.string.notification_channel_name)
            val descriptionText = ctx.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            channel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val manager =
                ctx.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}