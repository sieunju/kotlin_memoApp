package com.hmju.memo.ui.bindingadapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.ui.bindingadapter.FlexibleDefine.LONG_CLICK_DURATION
import com.hmju.memo.ui.bindingadapter.FlexibleDefine.isLongPress
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.ImageEditViewModel
import com.hmju.memo.widget.flexibleImageView.FlexibleImageView

/**
 * Description : 이미지 편집 페이지 관련 BindingAdapter Class
 *
 * Created by juhongmin on 2020/10/06
 */

object FlexibleDefine {
    val LONG_CLICK_DURATION: Long = 3000
    var isLongPress = false
}

@Suppress("DEPRECATION")
@BindingAdapter(value = ["viewModel", "rootView", "targetView"], requireAll = false)
fun setFlexibleListener(
    imageView: FlexibleImageView,
    viewModel: BaseViewModel,
    rootView: View,
    targetView: View
) {

//    imageView.setOnTouchListener { v, event ->
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                isLongPress = true
//                Handler().postDelayed(
//                    {
//                        if (isLongPress) {
//                            JLogger.d("LongClick!!")
//                            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//                            val data = ClipData.newPlainText("Label?", "Text???")
//                            val shadow = View.DragShadowBuilder(targetView)
//
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                targetView.startDragAndDrop(data, shadow, null, 0)
//                            } else {
//                                targetView.startDrag(data, shadow, null, 0)
//                            }
//
//                        }
//                    }, LONG_CLICK_DURATION
//                )
//            }
//            MotionEvent.ACTION_UP -> {
//                isLongPress = false
//            }
//        }
//        return@setOnTouchListener true
//    }

    imageView.setOnLongClickListener { view ->
        // 태그 생성
        val item = ClipData.Item(rootView.tag.toString())
        val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)

        val data = ClipData(rootView.tag.toString(), mimeType, item)
        val shadow = View.DragShadowBuilder(rootView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rootView.startDragAndDrop(data, shadow, rootView, 0)
        } else {
            rootView.startDrag(data, shadow, rootView, 0)
        }

        if (viewModel is ImageEditViewModel) {
            viewModel.isStartLeft = view.id == R.id.imgLeft
        }

        // 점차 사라지는 애니메이션 추가.
        ObjectAnimator.ofFloat(rootView, View.ALPHA, 1F, 0.25F).apply {
            duration = 1000
            start()
        }

        return@setOnLongClickListener true
    }

    rootView.setOnDragListener { v, event ->
        if (viewModel is ImageEditViewModel) {
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    if (v.id == R.id.clLeft) {
                        // Swipe Cancel
                        if (viewModel.isStartLeft) {
                            JLogger.d("다른 영역으로 옮기지 않았씁니다.")
                            // Alpha Animation
                            ObjectAnimator.ofFloat(v, View.ALPHA, 0.25F, 1F).apply {
                                duration = 1000
                                start()
                            }
                        } else {
                            // Swipe Success
                            JLogger.d("다른 영역으로 옮겼씁니다.")
                            viewModel.swipeImage(v, targetView)
                        }
                    } else {
                        // Swipe Success
                        if (viewModel.isStartLeft) {
                            JLogger.d("다른 영역으로 옮겼씁니다.")
                            viewModel.swipeImage(v, targetView)
                        } else {
                            // Swipe Cancel
                            JLogger.d("다른 영역으로 옮기지 않았씁니다.")
                            ObjectAnimator.ofFloat(v, View.ALPHA, 0.25F, 1F).apply {
                                duration = 1000
                                start()
                            }
                        }
                    }
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    JLogger.d("드래그 끄으으읕! ${v.tag}")
                    if (v.alpha != 1F) {
                        ObjectAnimator.ofFloat(v, View.ALPHA, 0.25F, 1F).apply {
                            duration = 1000
                            start()
                        }
                    }

                }
            }
        }

        return@setOnDragListener true
    }
}