package com.hmju.memo.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hmju.memo.R
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.ui.memo.MainActivity
import com.hmju.memo.utils.JLogger
import java.net.URLDecoder


/**
 * Description : Firebase Service
 *
 * Created by juhongmin on 2020/11/20
 */
class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        try {
            var title: CharSequence? = null
            var message: CharSequence? = null
            var imgUrl: String? = null
            var linkUrl: String? = null
            remoteMsg.notification?.also { notification ->
                JLogger.d("notification Setting ")
                if (!notification.title.isNullOrEmpty()) {
                    title = htmlText(notification.title!!)
                }

                if (!notification.body.isNullOrEmpty()) {
                    message = htmlText(notification.body!!)
                }

                if (notification.imageUrl != null) {
                    imgUrl = URLDecoder.decode(notification.imageUrl.toString(), ENC)
                }
            }

            remoteMsg.data.also { data ->
                JLogger.d("notification data ")
                if (!data[KEY_TITLE].isNullOrEmpty()) {
                    title = htmlText(data[KEY_TITLE]!!)
                }
                if (!data[KEY_MSG].isNullOrEmpty()) {
                    message = htmlText(data[KEY_MSG]!!)
                }
                if (!data[KEY_IMG_URL].isNullOrEmpty()) {
                    imgUrl = URLDecoder.decode(data[KEY_IMG_URL], ENC)
                }
                if (!data[KEY_LINK_URL].isNullOrEmpty()) {
                    linkUrl = URLDecoder.decode(data[KEY_LINK_URL], ENC)
                }
            }

            JLogger.d("onMessageReceived Title $title Message $message ImgUrl $imgUrl LinkUrl $linkUrl")

            // 제목은 필수값.
            if (title.isNullOrEmpty()) return

            val pushId = (System.currentTimeMillis() % Integer.MAX_VALUE).toInt()
            val builder = NotificationCompat.Builder(
                this,
                getString(R.string.notification_channel_id)
            ).apply {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    priority = NotificationManager.IMPORTANCE_HIGH
                }

                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle(title)
                setContentText(message ?: "메시지가 도착했습니다.")
                setAutoCancel(true)

                if (!linkUrl.isNullOrEmpty()) {
                    val intent = Intent(this@MyFirebaseService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(ExtraCode.PUSH_LINK_URL, linkUrl)
                    }
                    val pendingIntent =
                        PendingIntent.getActivity(
                            this@MyFirebaseService,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                    setContentIntent(pendingIntent)
                }
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // TextType
            if (imgUrl.isNullOrEmpty()) {
                builder.setStyle(NotificationCompat.BigTextStyle())
                manager.notify(pushId, builder.build())
            } else {
                Glide.with(this)
                    .asBitmap()
                    .load(imgUrl)
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val pictureStyle = NotificationCompat.BigPictureStyle()
                                .setBigContentTitle(title)
                                .setSummaryText(message)
                                .bigPicture(resource)
                            builder.setStyle(pictureStyle)
                            manager.notify(pushId, builder.build())
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            JLogger.d("onMessageReceived Error ${ex.message}")
        }
    }

    private fun htmlText(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            null
        }
    }


    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    companion object {
        const val ENC = "UTF-8"
        const val KEY_TITLE = "title"
        const val KEY_MSG = "message"
        const val KEY_IMG_URL = "imgUrl"
        const val KEY_LINK_URL = "linkUrl"

    }
}