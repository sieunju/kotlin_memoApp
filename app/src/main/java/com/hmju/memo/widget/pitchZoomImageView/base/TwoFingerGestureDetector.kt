package com.hmju.memo.widget.pitchZoomImageView.base

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration

/**
 * Description: TwoFinger Gesture Class
 *
 * Created by hmju on 2020-10-06
 */
abstract class TwoFingerGestureDetector(private val ctx: Context) : BaseGestureDetector() {

    private val mEdgeSlop: Float by lazy {
        val config = ViewConfiguration.get(ctx)
        return@lazy config.scaledEdgeSlop.toFloat()
    }

    private var rightSlopEdge: Float = 0F
    private var bottomSlopEdge: Float = 0F

    protected var prevFingerDiffX: Float = 0F
    protected var prevFingerDiffY: Float = 0F
    protected var currentFingerDiffX: Float = 0F
    protected var currentFingerDiffY: Float = 0F

    private var currentLen: Float = 0F
    private var prevLen: Float = 0F

    override fun updateStateByEvent(event: MotionEvent?) {
        super.updateStateByEvent(event)
        // Null Check
        if (event == null) return

        currentLen = -1F
        prevLen = -1F

        // Set Previous Event
        prevEvent?.let {
            if (it.pointerCount >= 2) {
                prevFingerDiffX = it.getX(1) - it.getX(0)
                prevFingerDiffY = it.getY(1) - it.getY(0)
            }
        }

        // Set Current DiffX, DiffY
        if (event.pointerCount >= 2) {
            currentFingerDiffX = event.getX(1) - event.getX(0)
            currentFingerDiffY = event.getY(1) - event.getY(0)
        }
    }

    /**
     * Check if we have a sloppy gesture. Sloppy gestures can happen if the edge
     * of the user's hand is touching the screen, for example.
     *
     * @param event
     * @return
     */
    protected fun isSloppyGesture(event: MotionEvent?): Boolean {
        // Null Check
        if (event == null) return false

        val metrics = ctx.resources.displayMetrics
        rightSlopEdge = metrics.widthPixels - mEdgeSlop
        bottomSlopEdge = metrics.heightPixels - mEdgeSlop

        val edgeSlop = mEdgeSlop
        val rightSlop = rightSlopEdge
        val bottomSlop = bottomSlopEdge

        val x0 = event.rawX
        val y0 = event.rawY
        val x1 = getRawX(event)
        val y1 = getRawY(event)

        val p0sloppy: Boolean =
            x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlop || y0 > bottomSlop
        val p1sloppy: Boolean =
            x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlop || y1 > bottomSlop

        return if (p0sloppy && p1sloppy) {
            true
        } else if (p0sloppy) {
            true
        } else p1sloppy
    }

    companion object {

        /**
         * MotionEvent has no getRawX(int) method; simulate it pending future API approval.
         * @param event MotionEvent
         * @param index Index Defalut 1
         * @return
         */
        private fun getRawX(event: MotionEvent?, index: Int = 1): Float {
            if (event == null) return 0F

            val offset = event.x - event.rawX
            return if (index < event.pointerCount) {
                event.getX(index) + offset
            } else {
                0F
            }
        }

        /**
         * MotionEvent has no getRawY(int) method; simulate it pending future API approval.
         * @param event MotionEvent
         * @param index Index Default 1
         * @return
         */
        private fun getRawY(event: MotionEvent?, index: Int = 1): Float {
            event?.let {
                val offset = it.y - it.rawY
                return if (index < it.pointerCount) {
                    it.getY(index) + offset
                } else {
                    0F
                }
            } ?: return 0F
        }

    }
}