package com.hmju.memo.widget.pitchZoomImageView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import com.hmju.memo.utils.JLogger
import com.hmju.memo.widget.pitchZoomImageView.gesture.MoveGestureDetector
import com.hmju.memo.widget.pitchZoomImageView.gesture.RotateGestureDetector
import kotlin.math.*

/**
 * Description : Pitch To Zoom ImageView Class
 *
 * Created by juhongmin on 2020/10/05
 */
class FlexibleImageView(private val ctx: Context, private val attrs: AttributeSet?) :
    AppCompatImageView(ctx, attrs) {
    enum class FlipDirection {
        NONE, VERTICAL, HORIZONTAL
    }

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

    private var scaleFactor = 1.0F
    private var focusX = 0F
    private var focusY = 0F
    private var rotationDegree = 0F
    private var flipDirection = FlipDirection.NONE
    private var flipX = 1F
    private var flipY = 1F

    private var isMultiTouch: Boolean = false
    private var moveDistance: Double = 0.0
    private var touchPoint = PointF()

    fun setFlip(direction: FlipDirection) {
        when (direction) {
            FlipDirection.HORIZONTAL -> {
                flipX *= -1
            }
            FlipDirection.VERTICAL -> {
                flipY *= -1
            }
            else -> {
                flipX = 1F
                flipY = 1F
            }
        }

        if (flipX > 1F && flipY > 1F) {
            flipDirection = FlipDirection.NONE
        } else if (flipX > 1F && flipY < 1F) {
            flipDirection = FlipDirection.VERTICAL
        } else if (flipX < 1F && flipY > 1F) {
            flipDirection = FlipDirection.HORIZONTAL
        }

        invalidate()
    }

    fun getFlipDirection() = flipDirection
    fun getFlipX() = flipX
    fun getFlipY() = flipY
    fun getScaleFactor() = scaleFactor

    fun setScaleFactor(scale: Float) {
        scaleFactor = scale
        invalidate()
    }

    fun setFocus(x: Float, y: Float) {
        focusX = x
        focusY = y
        invalidate()
    }

    fun moveFocus(x: Float, y: Float) {
        setFocus(x = focusX + x, y = focusY + y)
    }

    override fun performClick(): Boolean {
        return if (isMultiTouch || moveDistance > MAX_LONG_CLICK_DISTANCE) {
            false
        } else {
            super.performLongClick()
        }
    }

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

        // compute transfrom
        val prop = arrayOfNulls<PointerProperties>(ev.pointerCount)
        val cords = arrayOfNulls<PointerCoords>(ev.pointerCount)

        val firstCoords = PointerCoords()
        ev.getPointerCoords(0, firstCoords)

        val n = ev.pointerCount
        JLogger.d("onTouchEvent PointerCount $n")
        for (i in 0 until n) {
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

        val screenBaseMotionEvent = MotionEvent.obtain(
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

        scaleGestureDetector.onTouchEvent(screenBaseMotionEvent)
        rotateGestureDetector.onTouchEvent(screenBaseMotionEvent)
        moveGestureDetector.onTouchEvent(screenBaseMotionEvent)

        computeClickEvent(ev)
        super.onTouchEvent(ev)

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
        translationX = focusX
        translationY = focusY
        scaleY = scaleFactor * flipX
        scaleX = scaleFactor * flipY
        rotation = rotationDegree

        super.onDraw(canvas)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val mergedScaleFactor = scaleFactor * detector.scaleFactor
            if (mergedScaleFactor > MAX_SCALE_FACTOR || mergedScaleFactor < MIN_SCALE_FACTOR) {
                return false
            }

            // 최대 최소값 범위내로 처리.

            scaleFactor = mergedScaleFactor
            scaleFactor = MIN_SCALE_FACTOR.coerceAtLeast(scaleFactor)
            scaleFactor = MAX_SCALE_FACTOR.coerceAtMost(scaleFactor)

            return true
        }
    }

    inner class MoveListener : MoveGestureDetector.Companion.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            val delta = detector.focusDelta
            focusX += delta.x
            focusY += delta.y

            return true
        }
    }

    inner class RotateListener : RotateGestureDetector.Companion.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            rotationDegree -= detector.rotationsDegreesDelta
            return true
        }
    }
}