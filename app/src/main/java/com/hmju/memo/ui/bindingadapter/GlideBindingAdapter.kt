package com.hmju.memo.ui.bindingadapter

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
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
import java.util.*

/**
 * Description : 이미지 로더 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */


@BindingAdapter("bindImg")
fun bindingImg(
    imgView: AppCompatImageView,
    imgUrl: String?
) {
    val placeHolder = listOf<Int>(
        R.color.colorPlaceHolder_1,
        R.color.colorPlaceHolder_2,
        R.color.colorPlaceHolder_3,
        R.color.colorPlaceHolder_4)
    val ran = Random().nextInt(placeHolder.size)

    imgUrl?.let {
        if(it == "null") return@let

        Glide.with(imgView.context)
            .load(getUrl(it))
            .placeholder(placeHolder[ran])
            .thumbnail(0.1F)
            .transform(CenterCrop())
            .into(imgView)
    }
}

@BindingAdapter("bind_img_header")
fun bindingImgHeader(
    imgView: AppCompatImageView,
    imgUrl: String?
) {

    val placeHolder = listOf<Int>(
        R.color.colorPlaceHolder_1,
        R.color.colorPlaceHolder_2,
        R.color.colorPlaceHolder_3,
        R.color.colorPlaceHolder_4)
    val ran = Random().nextInt(placeHolder.size)

    imgUrl?.let{
        Glide.with(imgView.context)
            .load(getUrl(it))
            .placeholder(placeHolder[ran])
            .dontAnimate()
            .transform(CenterCrop())
            .error(R.drawable.ic_profile_default)
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

fun glideLoggerListener() : RequestListener<Drawable> {
    return object : RequestListener<Drawable> {

        override fun onResourceReady(resource: Drawable?,
                                     model: Any?,
                                     target: Target<Drawable>?,
                                     dataSource: DataSource?,
                                     isFirstResource: Boolean): Boolean {
//            if (resource is BitmapDrawable) {
//                val bitmap = resource.bitmap
//                JLogger.d("onResourceReady ${bitmap.byteCount} Width ${bitmap.width} Height ${bitmap.height}")
//            }

            JLogger.d("onResourceReady ${resource.toString()}")
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