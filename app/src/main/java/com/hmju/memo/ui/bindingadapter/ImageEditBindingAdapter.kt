package com.hmju.memo.ui.bindingadapter

import android.annotation.SuppressLint
import android.content.ClipData
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
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.ui.bindingadapter.FlexibleDefine.LONG_CLICK_DURATION
import com.hmju.memo.ui.bindingadapter.FlexibleDefine.isLongPress
import com.hmju.memo.utils.JLogger
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
@BindingAdapter(value = ["viewModel", "targetView"])
fun setFlexibleListener(
    imageView: FlexibleImageView,
    viewModel: BaseViewModel,
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

    imageView.setOnLongClickListener {view->
        JLogger.d("Long Click! Drag Start")
        val data = ClipData.newPlainText("Label?","Text???")
        val shadow = View.DragShadowBuilder(targetView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            targetView.startDragAndDrop(data,shadow,null,0)
        } else {
            targetView.startDrag(data,shadow,null,0)
        }

        return@setOnLongClickListener true
    }

    targetView.setOnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                JLogger.d("Action Drag Started")
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                // 범위 밖으로 나간경우.
                JLogger.d("Action Drag Exited")
            }

            DragEvent.ACTION_DROP -> {
                JLogger.d("Action Drop")
                return@setOnDragListener true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // 드레그 종료..
                JLogger.d("ActionDrop Ended")
                return@setOnDragListener true
            }
        }

        return@setOnDragListener true
    }
}