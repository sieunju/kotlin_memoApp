package com.hmju.memo.ui.widgt

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hmju.memo.R
import com.hmju.memo.ui.widgt.MemoAppWidget.Companion.appId
import com.hmju.memo.utils.JLogger

/**
 * Implementation of App Widget functionality.
 */
class MemoAppWidget : AppWidgetProvider() {

    companion object {
        var appId : Int? = 11
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        JLogger.d("onUpdate ${appWidgetIds[0]}")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateWidget(
    context: Context,
    logo: String,
    logoImg: Bitmap?,
    title: String?,
    contents: String?
) {

    val intent = (context as Activity).intent

    val appWidgetId = intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

    JLogger.d("위젯이 있나요?? $appWidgetId")
    JLogger.d("WidgetId $appId\n타이틀 $title\n내용 $contents")

    val views = RemoteViews(
        context.packageName,
        R.layout.memo_app_widget
    )

    logoImg?.let {
        views.setTextViewText(R.id.tv_logo, logo)
        views.setTextViewText(R.id.tv_title, title)
        views.setTextViewText(R.id.tv_contents, contents)
        views.setImageViewBitmap(R.id.img_logo,logoImg)
//        Glide.with(context)
//            .asBitmap()
//            .load(it)
//            .into(object : CustomTarget<Bitmap> (){
//                override fun onLoadCleared(placeholder: Drawable?) {
//                }
//
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    views.setImageViewBitmap(R.id.img_logo, resource)
//                    AppWidgetManager.getInstance(context).updateAppWidget(appId!!,views)
//                }
//            })

        AppWidgetManager.getInstance(context).updateAppWidget(appId!!,views)
    } ?: run {
        views.setTextViewText(R.id.tv_logo, logo)
        views.setTextViewText(R.id.tv_title, title)
        views.setTextViewText(R.id.tv_contents, contents)
        AppWidgetManager.getInstance(context).updateAppWidget(appId!!,views)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
//    val widgetText = context.getString(R.string.appwidget_text)
//    // Construct the RemoteViews object
//    val views = RemoteViews(context.packageName,
//        R.layout.memo_app_widget
//    )
//    views.setTextViewText(R.id.appwidget_text, widgetText)
//
//    // Instruct the widget manager to update the widget
//    appWidgetManager.updateAppWidget(appWidgetId, views)

    val views = RemoteViews(
        context.packageName,
        R.layout.memo_app_widget
    )
    views.setTextViewText(R.id.tv_logo, "로고")
    views.setTextViewText(R.id.tv_title, "제목")
    views.setTextViewText(R.id.tv_contents, context.getString(R.string.large_text))
    appWidgetManager.updateAppWidget(appWidgetId, views)
    appId = appWidgetId
}