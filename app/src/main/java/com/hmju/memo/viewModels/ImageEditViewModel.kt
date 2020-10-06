package com.hmju.memo.viewModels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.utils.ResourceProvider

/**
 * Description:
 *
 * Created by hmju on 2020-10-06
 */
class ImageEditViewModel(
    val photoUri : String,
    private val provider: ResourceProvider
) : BaseViewModel(){

    val startCropImage = SingleLiveEvent<Bitmap>()

    fun cropImage(view : View) {
        val bitmap = Bitmap.createBitmap(view.measuredWidth,view.measuredHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        startCropImage.value = bitmap
    }
}