package com.hmju.memo.ui.bindingadapter

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hmju.memo.R
import com.hmju.memo.define.NetInfo
import com.hmju.memo.utils.JLogger

/**
 * Description : 이미지 로더 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */


@BindingAdapter("bindingImgUrl")
fun bindingImg(
    imgView: AppCompatImageView,
    imgUrl: String?
) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(getUrl(it))
            .placeholder(R.color.colorPrimary)
            .listener(glideLoggerListener("original"))
            .override(200,200)
            .transform(CenterCrop())
            .into(imgView)
    }
}

fun getUrl(url: String?): String? {
    url?.let {
        return when {
            it.startsWith("http") -> {
                it
            }
            // 정상 적인 Path 값인 경우.
            it.startsWith("/") -> {
                NetInfo.BASE_URL + NetInfo.IMG_PATH + it
            }
            // 정상 적인 Path 값이 아닌경우
            else -> {
                NetInfo.BASE_URL + NetInfo.IMG_PATH + "/" + it
            }
        }
    } ?: run {
        return ""
    }
}

fun glideLoggerListener(name: String) : RequestListener<Drawable> {
    return object : RequestListener<Drawable> {

        override fun onResourceReady(resource: Drawable?,
                                     model: Any?,
                                     target: Target<Drawable>?,
                                     dataSource: DataSource?,
                                     isFirstResource: Boolean): Boolean {
            if (resource is BitmapDrawable) {
                val bitmap = resource.bitmap
                JLogger.d("onResourceReady ${bitmap.byteCount} Width ${bitmap.width} Height ${bitmap.height}")
            }
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            JLogger.d("onLoadFailed Fail${e?.message} \tModel\t$model" )
            return false
        }
    }
}