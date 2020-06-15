package com.hmju.memo.ui.bindingadapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hmju.memo.define.NetInfo

/**
 * Description : 이미지 로더 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */


@BindingAdapter("bindingImgUrl")
fun bindingImg(
    imgView: AppCompatImageView,
    imgUrl : String?
){
    imgUrl?.let{
        Glide.with(imgView.context)
            .load(getUrl(it))
            .transform(CenterCrop())
            .into(imgView)
    }
}

fun getUrl(url: String?): String? {
    url?.let{
        return when {
            it.startsWith("http") -> {
                it
            }
            // 정상 적인 Path 값인 경우.
            it.startsWith("/") -> {
                NetInfo.BASE_URL + it
            }
            // 정상 적인 Path 값이 아닌경우
            else -> {
                NetInfo.BASE_URL + "/" + it
            }
        }
    } ?: run {
        return ""
    }
}