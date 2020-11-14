package com.hmju.memo.widget.keyboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Service
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.hmju.memo.utils.JLogger

object FluidContentResize {

    private var heightAnimator: ValueAnimator = ObjectAnimator()
    private var isKeyboardShow = false
    private lateinit var imm : InputMethodManager

    fun listen(activity: Activity) {
        val viewHolder = ActivityViewHolder.createFrom(activity)
        imm = activity.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager

        KeyboardVisibilityDetector.listen(viewHolder) {
            isKeyboardShow = it.visible
            JLogger.d("Keyboard Changed ${it.contentHeight} NonLayout ${it.contentHeightBeforeResize} Visible ${it.visible}")
            animateHeight(viewHolder, it)
        }
        // 화면 꺼질때.
        viewHolder.onDetach {
            heightAnimator.cancel()
            heightAnimator.removeAllUpdateListeners()
        }
    }

    fun closeSoftKeyboard() {
        if(isKeyboardShow) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
            isKeyboardShow = false
        }
    }

    private fun animateHeight(viewHolder: ActivityViewHolder, event: KeyboardVisibilityChanged) {
        val contentView = viewHolder.contentView
        contentView.setHeight(event.contentHeightBeforeResize)

        heightAnimator.cancel()

        // Warning: animating height might not be very performant. Try turning on
        // "Profile GPI rendering" in developer options and check if the bars stay
        // under 16ms in your app. Using Transitions API would be more efficient, but
        // for some reason it skips the first animation and I cannot figure out why.
        heightAnimator = ObjectAnimator.ofInt(event.contentHeightBeforeResize, event.contentHeight).apply {
            interpolator = FastOutSlowInInterpolator()
            duration = 300
        }
        heightAnimator.addUpdateListener { contentView.setHeight(it.animatedValue as Int) }
        heightAnimator.start()
    }

    private fun View.setHeight(height: Int) {
        val params = layoutParams
        params.height = height
        layoutParams = params
    }
}