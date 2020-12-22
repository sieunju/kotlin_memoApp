package com.hmju.memo.viewmodels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.io
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import io.reactivex.Observable

/**
 * Description:
 *
 * Created by hmju on 2020-10-06
 */
class ImageEditViewModel(
    photoList: ArrayList<String>,
    private val provider: ImageFileProvider
) : BaseViewModel() {

    val leftPhotoPath = NonNullMutableLiveData(photoList[0])
    val rightPhotoPath = NonNullMutableLiveData(photoList[1])
    val startResetImage = SingleLiveEvent<Unit>()
    val startSwitchImage = SingleLiveEvent<Unit>()
    val startContentAni = SingleLiveEvent<Boolean>()

    var isStartLeft = false // 드래그를 왼쪽에서 시작 했는지 유무 변수

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
                }.io()
                .flatMap {
                    view.draw(Canvas(it))
                    Observable.just(provider.bitmapToFile(it))
                }.io()
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

    /**
     * Start Drag
     * @param view 드래그 할 View
     */
    fun startDragAndDrop(view: View) {
        isStartLeft = view.id == R.id.imgLeft
        startContentAni.value = false
    }

    /**
     * onDrag And Drop
     * @param view Drop 한 위치 View
     */
    fun onDragAndDrop(view: View) {
        // Drag And Drop 한 위치가 왼쪽인 경우.
        if (view.id == R.id.clLeft) {
            // Switch Success
            if (!isStartLeft) {
                switchImage()
            }
        } else if (view.id == R.id.clRight) {
            // Drag And Drop 한 위치가 오른쪽인 경우.

            // Switch Success
            if (isStartLeft) {
                switchImage()
            }
        }

        // Start Ani
        startContentAni.value = true
    }

    fun onDragNothing() {
        // Start Ani
        startContentAni.value = true
    }

    /**
     * 좌 / 우 이미지 전환
     *
     */
    fun switchImage() {
        startSwitchImage.call()
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