package com.hmju.memo.ui.bindingadapter

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.hmju.memo.R
import com.hmju.memo.define.NetInfo
import com.hmju.memo.utils.JLogger
import com.hmju.memo.widget.glide.GlideUtil
import com.hmju.memo.widget.glide.transformation.BaseTransformation

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
                // resource/img Type
                String.format("%s/%s", NetInfo.BASE_URL, it)
            }
            it.startsWith("/${NetInfo.IMG_PATH}") -> {
                // /resource/img Type
                String.format("%s%s", NetInfo.BASE_URL, it)
            }
            it.startsWith("content://") -> {
                // Local File Path
                it
            }
            else -> {
                String.format("%s/%s/%s",NetInfo.BASE_URL,NetInfo.IMG_PATH,it)
            }
        }
    } ?: run {
        return ""
    }
}