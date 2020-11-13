package com.hmju.memo.ui.bindingadapter

import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.hmju.memo.widget.glide.GlideUtil
import java.util.*

/**
 * Http Url 로 이미지 가져오는 경우
 * 사용 함수.
 */
@BindingAdapter("bindImg")
fun bindingImg(
    imgView: AppCompatImageView,
    imgUrl: String?
) {
    imgUrl?.let {
        GlideUtil.with()
            .load(getUrl(it))
            .scaleType(GlideUtil.ScaleType.CENTER_CROP)
            .into(imgView)
    }
}

/**
 * 로컬에서 가져오는 경우
 * 사용 함수.
 */
@BindingAdapter("bindImgGallery")
fun bindingImgGallery(
    imgView: AppCompatImageView,
    imgUrl: String?
) {
    imgUrl?.let {
        GlideUtil.with()
            .load(it)
            .scaleType(GlideUtil.ScaleType.CENTER_CROP)
            .failDrawable(R.drawable.bg_res_error)
            .into(imgView)
    }
}

@BindingAdapter("bindUri")
fun bindingImgMj(
    imgView: AppCompatImageView,
    imgUri: String?
) {

    imgUri?.let {
        GlideUtil.with()
            .load(it)
            .scaleType(GlideUtil.ScaleType.FIT_CENTER)
            .failDrawable(R.drawable.bg_res_error)
            .into(imgView)
    }
}

/**
 * Glide ImageLoader
 * 각 Url 맞게 가공해서 리턴 하는 함수.
 */
fun getUrl(url: String?): String {
    url?.let {
        return when {
            it.startsWith("http") -> {
                it
            }
            it.startsWith(NetInfo.IMG_PATH) -> {
                String.format("%s/%s", NetInfo.BASE_URL, it)
            }
            // 앞에 '/' 있는 경우
            it.startsWith("/") -> {
                // www.example.com/resource/imgPath
                String.format("%s/%s%s", NetInfo.BASE_URL, NetInfo.IMG_PATH, it)
            }
            // 정상 적인 Path 값이 아닌경우
            else -> {
                // www.example.com/resource/imagePath
                String.format("%s/%s/%s", NetInfo.BASE_URL, NetInfo.IMG_PATH, it)
            }
        }
    } ?: run {
        return ""
    }
}