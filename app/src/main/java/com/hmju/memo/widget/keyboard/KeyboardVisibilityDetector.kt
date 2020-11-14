package com.hmju.memo.widget.keyboard

import android.view.ViewTreeObserver

/**
 * Description : 키보드 올라오는것을 자연스럽게 표현하기 위한 Open Library 가져옴.
 * @link{https://github.com/saket/FluidKeyboardResize}
 * Created by juhongmin on 2020/09/09
 */
object KeyboardVisibilityDetector {

    fun listen(viewHolder: ActivityViewHolder, listener: (KeyboardVisibilityChanged) -> Unit) {
        val detector = Detector(viewHolder, listener)
        viewHolder.nonResizableLayout.viewTreeObserver.addOnPreDrawListener(detector)
        viewHolder.onDetach {
            viewHolder.nonResizableLayout.viewTreeObserver.removeOnPreDrawListener(detector)
        }
    }

    private class Detector(
        val viewHolder: ActivityViewHolder,
        val listener: (KeyboardVisibilityChanged) -> Unit
    ) : ViewTreeObserver.OnPreDrawListener {

        private var previousHeight: Int = -1

        override fun onPreDraw(): Boolean {
            val detected = detect()

            // The layout flickers for a moment, usually on the first
            // animation. Intercepting this pre-draw seems to solve the problem.
            return detected.not()
        }

        private fun detect(): Boolean {
            val contentHeight = viewHolder.resizableLayout.height
            if (contentHeight == previousHeight) {
                return false
            }

            if (previousHeight != -1) {
                val statusBarHeight = viewHolder.resizableLayout.top
//                val isKeyboardVisible = contentHeight < viewHolder.nonResizableLayout.height - statusBarHeight
                val isKeyboardVisible = contentHeight < previousHeight

                listener(KeyboardVisibilityChanged(
                    visible = isKeyboardVisible,
                    contentHeight = contentHeight,
                    contentHeightBeforeResize = previousHeight))
            }

            previousHeight = contentHeight
            return true
        }
    }
}