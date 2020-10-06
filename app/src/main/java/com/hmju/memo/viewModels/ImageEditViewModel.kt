package com.hmju.memo.viewModels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.utils.ResourceProvider

/**
 * Description:
 *
 * Created by hmju on 2020-10-06
 */
class ImageEditViewModel(
    private val photoList: ArrayList<String>,
    private val provider: ResourceProvider
) : BaseViewModel() {

    val beforePhotoPath = NonNullMutableLiveData(photoList[0])
    val afterPhotoPath = NonNullMutableLiveData(photoList[1])

    val startCropImage = SingleLiveEvent<Bitmap>()
    val startCopyImage = SingleLiveEvent<Bitmap>()

    fun cropImage(view: View) {
        // Copy Bitmap
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        startCropImage.value = bitmap
    }

    fun longClickTest(view: View){
        // Copy Bitmap
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        startCopyImage.value = bitmap
    }
}