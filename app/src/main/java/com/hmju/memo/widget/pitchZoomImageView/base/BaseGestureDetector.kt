package com.hmju.memo.widget.pitchZoomImageView.base

import android.annotation.SuppressLint
import android.view.MotionEvent


/**
 * Description:
 *
 * Created by hmju on 2020-10-06
 */
abstract class BaseGestureDetector {

    protected var gestureInProgress = false

    protected var prevEvent: MotionEvent? = null
    private var currentEvent: MotionEvent? = null

    protected var currentPressure = 0f
    protected var prevPressure = 0f
    /**
     * Return the time difference in milliseconds between the previous accepted
     * GestureDetector event and the current GestureDetector event.
     *
     * @return Time difference since the last move event in milliseconds.
     */
    protected var timeDelta: Long = 0


    /**
     * This value is the threshold ratio between the previous combined pressure
     * and the current combined pressure. When pressure decreases rapidly
     * between events the position values can often be imprecise, as it usually
     * indicates that the user is in the process of lifting a pointer off of the
     * device. This value was tuned experimentally.
     */
    protected val PRESSURE_THRESHOLD = 0.67F

    /**
     * All gesture detectors need to be called through this method to be able to
     * detect gestures. This method delegates work to handler methods
     * (handleStartProgressEvent, handleInProgressEvent) implemented in
     * extending classes.
     *
     * @param event
     * @return
     */
    open fun onTouchEvent(event: MotionEvent): Boolean {
        val actionCode = event.action and MotionEvent.ACTION_MASK
        if (!gestureInProgress) {
            handleStartProgressEvent(actionCode, event)
        } else {
            handleInProgressEvent(actionCode, event)
        }
        return true
    }

    /**
     * Called when the current event occurred when NO gesture is in progress
     * yet. The handling in this implementation may set the gesture in progress
     * (via mGestureInProgress) or out of progress
     * @param actionCode
     * @param event
     */
    protected abstract fun handleStartProgressEvent(actionCode: Int, event: MotionEvent?)

    /**
     * Called when the current event occurred when a gesture IS in progress. The
     * handling in this implementation may set the gesture out of progress (via
     * mGestureInProgress).
     * @param actionCode
     * @param event
     */
    protected abstract fun handleInProgressEvent(actionCode: Int, event: MotionEvent?)


    @SuppressLint("Recycle")
    protected open fun updateStateByEvent(event: MotionEvent?) {
        // Null Check
        if (event == null) return
        prevEvent?.let { prevEvent ->
            // Reset currentEvent
            currentEvent?.recycle()
            currentEvent = null
            currentEvent = MotionEvent.obtain(event)

            timeDelta = event.eventTime - prevEvent.eventTime

            // Pressure
            currentPressure = event.getPressure(event.actionIndex)
            prevPressure = prevEvent.getPressure(prevEvent.actionIndex)
        }
    }

    /**
     * Reset State Func.
     */
    protected open fun resetState() {
        prevEvent?.recycle()
        prevEvent = null
        currentEvent?.recycle()
        currentEvent = null

        gestureInProgress = false
    }
}