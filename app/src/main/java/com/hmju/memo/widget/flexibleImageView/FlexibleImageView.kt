package com.hmju.memo.widget.flexibleImageView

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import com.hmju.memo.R
import com.hmju.memo.widget.flexibleImageView.base.FlexibleStateItem
import com.hmju.memo.widget.flexibleImageView.gesture.MoveGestureDetector
import com.hmju.memo.widget.flexibleImageView.gesture.RotateGestureDetector
import kotlin.math.*

/**
 * Description : Scale, Move, Rotate 가능한 ImageView Class
 *
 * Created by juhongmin on 2020/10/05
 */
class FlexibleImageView(private val ctx: Context, attrs: AttributeSet?) :
    AppCompatImageView(ctx, attrs) {

    companion object {
        const val MAX_SCALE_FACTOR = 10.0F
        const val MIN_SCALE_FACTOR = 0.3F
        const val MAX_CLICK_DISTANCE = 4
        const val MAX_LONG_CLICK_DISTANCE = 16
    }

    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(ctx, ScaleListener())
    }

    private val moveGestureDetector: MoveGestureDetector by lazy {
        MoveGestureDetector(ctx, MoveListener())
    }

    private val rotateGestureDetector: RotateGestureDetector by lazy {
        RotateGestureDetector(ctx, RotateListener())
    }

    var stateItem = FlexibleStateItem(
        scale = 1.0F,
        focusX = 0F,
        focusY = 0F,
        rotationDegree = 0F,
        flipX = 1F,
        flipY = 1F
    )

//    private var scaleFactor = 1.0F
//    private var focusX = 0F
//    private var focusY = 0F
//    private var rotationDegree = 0F
//    private var flipX = 1F
//    private var flipY = 1F

    private var isMultiTouch: Boolean = false
    private var moveDistance: Double = 0.0
    private var touchPoint = PointF()

    init {
        if (isInEditMode) throw IllegalArgumentException("isInEditMode true...!!")

        // 속성 값 세팅
        attrs?.let {
            val attr: TypedArray = ctx.obtainStyledAttributes(it, R.styleable.FlexibleImageView)
            attr.recycle()
        }
    }

    fun resetView() {
        stateItem.reset()
        isMultiTouch = false
        moveDistance = 0.0
        touchPoint = PointF()
        alpha = 1F
        invalidate()
    }

    /**
     * Scale, Focus, Rotate 상태값 전환후 다시 그리는 함수.
     * @param tmpStateItem 전환할 State Item.
     */
    fun switchingState(tmpStateItem : FlexibleStateItem) {
        stateItem = tmpStateItem
        invalidate()
    }

    fun setScaleFactor(scale: Float) {
        stateItem.scale = scale
        invalidate()
    }

    private fun setFocus(x: Float, y: Float) {
        stateItem.focusX = x
        stateItem.focusY = y
        invalidate()
    }

    fun moveFocus(x: Float, y: Float) {
        setFocus(x = stateItem.focusX + x, y = stateItem.focusY + y)
    }

//    override fun performClick(): Boolean {
//        return if (isMultiTouch || moveDistance > MAX_LONG_CLICK_DISTANCE) {
//            false
//        } else {
//            super.performLongClick()
//        }
//    }

    override fun performLongClick(): Boolean {
        return if (isMultiTouch || moveDistance > MAX_LONG_CLICK_DISTANCE) {
            false
        } else {
            super.performLongClick()
        }
    }

    private fun getRowPoint(ev: MotionEvent, index: Int, point: PointF) {
        val location = intArrayOf(0, 0)
        getLocationOnScreen(location)

        var x = ev.getX(index)
        var y = ev.getY(index)

        x *= scaleX
        y *= scaleY

        var angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()))
        angle += rotation

        val length = PointF.length(x, y)
        x = (length * cos(Math.toRadians(angle)) + location[0]).toFloat()
        y = (length * sin(Math.toRadians(angle)) + location[1]).toFloat()

        point.set(x, y)
    }

    @SuppressLint("Recycle")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

//        JLogger.d("onTouchEvent!!!!!")

        // compute trans from
        val prop = arrayOfNulls<PointerProperties>(ev.pointerCount)
        val cords = arrayOfNulls<PointerCoords>(ev.pointerCount)

        // get First Coords
        ev.getPointerCoords(0, PointerCoords())

        for (i in 0 until ev.pointerCount) {
            val properties = PointerProperties()
            ev.getPointerProperties(i, properties)
            prop[i] = properties

            val cod = PointerCoords()
            ev.getPointerCoords(i, cod)

            val rawPos = PointF()
            getRowPoint(ev, i, rawPos)
            cod.x = rawPos.x
            cod.y = rawPos.y
            cords[i] = cod
        }

        val baseMotionEvent = MotionEvent.obtain(
            ev.downTime,
            ev.eventTime,
            ev.action,
            ev.pointerCount,
            prop,
            cords,
            ev.metaState,
            ev.buttonState,
            ev.xPrecision,
            ev.xPrecision,
            ev.deviceId,
            ev.edgeFlags,
            ev.source,
            ev.flags
        )

        scaleGestureDetector.onTouchEvent(baseMotionEvent)
        rotateGestureDetector.onTouchEvent(baseMotionEvent)
        moveGestureDetector.onTouchEvent(baseMotionEvent)

        computeClickEvent(ev)
        super.onTouchEvent(ev)

        // Canvas Draw
        invalidate()
        return true
    }

    private fun computeClickEvent(ev: MotionEvent) {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isMultiTouch = ev.pointerCount >= 2
                touchPoint = PointF(ev.rawX, ev.rawY)
            }
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (ev.pointerCount > 1) {
                    isMultiTouch = true
                    return
                }

                moveDistance = getDistance(PointF(ev.rawX, ev.rawY), touchPoint)

            }
        }
    }

    private fun getDistance(point1: PointF, point2: PointF): Double {
        return sqrt(
            (point1.x - point2.x).toDouble().pow(2.0) + (point1.y - point2.y).toDouble()
                .pow(2.0)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        translationX = stateItem.focusX
        translationY = stateItem.focusY
        scaleY = stateItem.scaleX
        scaleX = stateItem.scaleY
        rotation = stateItem.rotationDegree

        super.onDraw(canvas)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val mergedScaleFactor = stateItem.scale * detector.scaleFactor

            // 범위 를 넘어 가는 경우 false 리턴.
            if (mergedScaleFactor <= MIN_SCALE_FACTOR || mergedScaleFactor >= MAX_SCALE_FACTOR) {
                return false
            }

            stateItem.scale = mergedScaleFactor

            return true
        }
    }

    inner class MoveListener : MoveGestureDetector.Companion.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            val delta = detector.focusDelta
            stateItem.focusX += delta.x
            stateItem.focusY += delta.y

            return true
        }
    }

    inner class RotateListener : RotateGestureDetector.Companion.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            stateItem.rotationDegree -= detector.rotationsDegreesDelta
            return true
        }
    }
}