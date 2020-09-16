package com.hmju.memo.widget.swipeLayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Description :
 *
 * Created by hmju on 2020-09-14
 */
class SwipeLayout : ConstraintLayout {

    companion object {
        enum class State (type : Int) {
            CLOSE(0),
            CLOSING(1),
            OPEN(2),
            OPENING(3),
            DRAGGING(4)
        }

        enum class Drag(type : Int) {
            LEFT(0x1),
            RIGHT(0x1.shl(1)),
            TOP(0x1.shl(2)),
            BOTTOM(0x1.shl(3))
        }

        const val DEFAULT_MIN_FLING_VELOCITY = 300
        const val DEFAULT_MIN_DIST_REQUEST_DISALLOW_PARENT = 1
    }

    interface SwipeListener {
        fun onClosed(view : SwipeLayout)
        fun onOpened(view : SwipeLayout)
        fun onSlide(view: SwipeLayout, offset : Float)
    }

    class SimpleSwipeListener : SwipeListener {
        override fun onClosed(view: SwipeLayout) {}

        override fun onOpened(view: SwipeLayout) {}

        override fun onSlide(view: SwipeLayout, offset: Float) {}
    }

    constructor(ctx: Context, attrs : AttributeSet?) : super(ctx,attrs) {

    }



    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}