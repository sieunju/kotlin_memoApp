package com.hmju.memo.widget.flexibleImageView.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import com.hmju.memo.widget.flexibleImageView.base.TwoFingerGestureDetector
import kotlin.math.atan2

/**
 * Description: Rotate Gesture Detector Class
 *
 * Created by hmju on 2020-10-06
 */
class RotateGestureDetector(
    private val ctx: Context,
    private val listener: OnRotateGestureListener
) : TwoFingerGestureDetector(ctx) {

    interface OnRotateGestureListener {
        fun onRotate(detector: RotateGestureDetector): Boolean
        fun onRotateBegin(detector: RotateGestureDetector): Boolean
        fun onRotateEnd(detector: RotateGestureDetector)
    }

    companion object {
        open class SimpleOnRotateGestureListener : OnRotateGestureListener {
            override fun onRotate(detector: RotateGestureDetector) = false

            override fun onRotateBegin(detector: RotateGestureDetector) = true

            override fun onRotateEnd(detector: RotateGestureDetector) {}
        }
    }

    private var mSloppyGesture: Boolean = false
    val rotationsDegreesDelta: Float
        get() {
            val diffRadians = atan2(prevFingerDiffY.toDouble(), prevFingerDiffX.toDouble()) -
                    atan2(currentFingerDiffY.toDouble(), currentFingerDiffX.toDouble())
            return (diffRadians * 180 / Math.PI).toFloat()
        }

    @SuppressLint("Recycle")
    override fun handleStartProgressEvent(actionCode: Int, event: MotionEvent?) {
        if (event == null) return

        when (actionCode) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                resetState()
                prevEvent = MotionEvent.obtain(event)
                timeDelta = 0

                updateStateByEvent(event)

                mSloppyGesture = isSloppyGesture(event)

                if (!mSloppyGesture) {
                    gestureInProgress = listener.onRotateBegin(this)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!mSloppyGesture) {
                    return
                }

                // See if we still have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event)
                if (!mSloppyGesture) {
                    // No, Start Normal Gesture Now
                    gestureInProgress = listener.onRotateBegin(this)
                }
            }
        }
    }

    @SuppressLint("Recycle")
    override fun handleInProgressEvent(actionCode: Int, event: MotionEvent?) {
        if (event == null) return

        when (actionCode) {
            MotionEvent.ACTION_POINTER_UP -> {
                // Gesture ended but
                updateStateByEvent(event)

                if (!mSloppyGesture) {
                    listener.onRotateEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (!mSloppyGesture) {
                    listener.onRotateEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_MOVE -> {
                updateStateByEvent(event)

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (currentPressure / prevPressure > PRESSURE_THRESHOLD) {
                    val updatePrevious = listener.onRotate(this)
                    if (updatePrevious) {
                        prevEvent?.recycle()
                        prevEvent = MotionEvent.obtain(event)
                    }
                }

            }
        }
    }

    override fun resetState() {
        super.resetState()
        mSloppyGesture = false
    }
}