package com.hmju.memo.viewModels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.convenience.to
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Observable
import java.io.File

/**
 * Description:
 *
 * Created by hmju on 2020-10-06
 */
class ImageEditViewModel(
    private val photoList: ArrayList<String>,
    private val provider: ResourceProvider
) : BaseViewModel() {

    val leftPhotoPath = NonNullMutableLiveData(photoList[0])
    val rightPhotoPath = NonNullMutableLiveData(photoList[1])
    val startResetImage = SingleLiveEvent<Unit>()
    val startSwitchImage = SingleLiveEvent<Pair<Bitmap, Bitmap>>()

    var isStartLeft = false // LongClick 분기 처리를 위한 변수.

    /**
     * 이미지 영역 합치는 함수.
     * @param view 머지 할 View.
     */
    fun mergeImage(view: View) {
        launch {
            Observable.just(view)
                .doOnSubscribe { onLoading() }
                .flatMap {
                    Observable.just(
                        Bitmap.createBitmap(
                            it.measuredWidth,
                            it.measuredHeight,
                            Bitmap.Config.ARGB_8888
                        )
                    )
                }.to()
                .flatMap {
                    view.draw(Canvas(it))
                    Observable.just(provider.getFile(it))
                }.to()
                .doOnComplete { onSuccess() }
                .subscribe({
                    it?.let { file ->
                        if (file.isFile) {
                            JLogger.d("파일 입니다.")
                        } else {
                            JLogger.d("파일이 아닙니다.")
                        }
                    }
                }, {
                    JLogger.d("Error ${it.message}")
                })
        }
    }

    fun swipeImage(currentView: View, otherView: View) {
        val currentBitmap = Bitmap.createBitmap(
            currentView.measuredWidth,
            currentView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        currentView.draw(Canvas(currentBitmap))
        val otherBitmap = Bitmap.createBitmap(
            otherView.measuredWidth,
            otherView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        otherView.draw(Canvas(otherBitmap))

        // 왼쪽에서 시작 했으면 CurrentView -> leftView
        if (isStartLeft) {
            startSwitchImage.value = Pair(currentBitmap, otherBitmap)
        } else {
            startSwitchImage.value = Pair(otherBitmap, currentBitmap)
        }
        startResetImage.call()
    }

    /**
     * 이미지 원래대로 돌리는 함수.
     */
    fun resetPhoto() {
        val leftPath = leftPhotoPath.value
        val rightPath = rightPhotoPath.value
        leftPhotoPath.postValue(leftPath)
        rightPhotoPath.postValue(rightPath)

        startResetImage.call()
    }
}