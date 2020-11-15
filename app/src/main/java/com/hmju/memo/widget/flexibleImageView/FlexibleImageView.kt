package com.hmju.memo.widget.flexibleImageView

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import com.hmju.memo.R
import com.hmju.memo.utils.JLogger
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

    private var isMultiTouch: Boolean = false
    private var moveDistance: Double = 0.0
    private var touchPoint = PointF()
    private var viewWidth = -1
    private var viewHeight = -1

    init {
        if (isInEditMode) throw IllegalArgumentException("isInEditMode true...!!")

        // width height get
        post {
            viewWidth = width
            viewHeight = height
        }
    }

    fun resetView() {
        stateItem.reset()
        isMultiTouch = false
        moveDistance = 0.0
        touchPoint = PointF()
        alpha = 1F
    }

    /**
     * set Image Width / Height
     * @param width Image Width
     * @param height Image Height
     */
    fun setImageSize(width: Int, height: Int) {
        stateItem.imgWidth = width
        stateItem.imgHeight = height
    }

    /**
     * set Min Scale
     * @param scale Min Scale
     */
    fun setCropScale(scale: Float) {
        stateItem.scale = scale
        stateItem.minScale = scale
        invalidate()
    }

    /**
     * Scale, Focus, Rotate 상태값 전환후 다시 그리는 함수.
     * @param tmpStateItem 전환할 State Item.
     */
    fun switchingState(tmpStateItem: FlexibleStateItem) {
        stateItem = tmpStateItem
        invalidate()
    }

    private fun setFocus(x: Float, y: Float) {
        stateItem.focusX = x
        stateItem.focusY = y
        invalidate()
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
//        rotateGestureDetector.onTouchEvent(baseMotionEvent)
        moveGestureDetector.onTouchEvent(baseMotionEvent)

        computeClickEvent(ev)
        super.onTouchEvent(ev)

        // Canvas Draw
        invalidate()
        return true
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

    override fun onDraw(canvas: Canvas?) {
        translationX = stateItem.focusX
        translationY = stateItem.focusY
        scaleY = stateItem.scaleX
        scaleX = stateItem.scaleY
        rotation = stateItem.rotationDegree

        super.onDraw(canvas)
    }

    /**
     * Image 위치값 연산 처리 함수.
     */
    private fun computeImageLocation(): RectF? {
        if (viewWidth == -1 || viewHeight == -1 ||
            stateItem.currentImgWidth == -1F ||
            stateItem.currentImgHeight == -1F
        ) return null

        val imgWidth = stateItem.currentImgWidth
        val imgHeight = stateItem.currentImgHeight
        val focusX = stateItem.focusX
        val focusY = stateItem.focusY

        val imgTop = (focusY + (viewHeight / 2F)) - imgHeight / 2F
        val imgLeft = (focusX + (viewWidth / 2F)) - imgWidth / 2F
        val imgRight = (focusX + (viewWidth / 2F)) + imgWidth / 2F
        val imgBottom = (focusY + (viewHeight / 2F)) + imgHeight / 2F

        return RectF(imgLeft, imgTop, imgRight, imgBottom)
    }

    /**
     * View 영역 밖으로 나갔는지 유무 함수.
     * @param rect Current Image Location
     */
    private fun computeInBoundary(rect: RectF): Pair<Float, Float>? {
        var diffFocusX = 0F
        var diffFocusY = 0F

        if (rect.left > 0) {
            diffFocusX -= Math.abs(rect.left)
        } else if (rect.right < viewWidth) {
            diffFocusX += Math.abs(rect.right - viewWidth)
        }

        if (rect.top > 0) {
            diffFocusY -= Math.abs(rect.top)
        } else if (rect.bottom < viewHeight) {
            diffFocusY += Math.abs(rect.bottom - viewHeight)
        }

        // 변경점이 없으면 아래 로직 패스 한다.
        if (diffFocusX == 0F && diffFocusY == 0F) {
            return null
        }

        return Pair(diffFocusX, diffFocusY)
    }

    private fun getDistance(point1: PointF, point2: PointF): Double {
        return sqrt(
            (point1.x - point2.x).toDouble().pow(2.0) + (point1.y - point2.y).toDouble()
                .pow(2.0)
        )
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        var prevScale = 0.5F

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = stateItem.scale * detector.scaleFactor

            prevScale = scale

            // 범위 를 넘어 가는 경우 false 리턴.
            if (scale <= MIN_SCALE_FACTOR || scale >= MAX_SCALE_FACTOR) {
                return false
            }

            stateItem.scale = scale

            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            // 이미지 확대 축소 제한
            if (prevScale < stateItem.minScale) {
                stateItem.scale = stateItem.minScale
                invalidate()
            }
        }
    }

    inner class MoveListener : MoveGestureDetector.Companion.SimpleOnMoveGestureListener() {

        override fun onMoveBegin(detector: MoveGestureDetector): Boolean {
            return true
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            val delta = detector.currentFocus
            stateItem.focusX += delta.x
            stateItem.focusY += delta.y
            return true
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            computeImageLocation()?.also { rect ->
                val pair = computeInBoundary(rect) ?: return

                if (pair.first != 0F) {
                    stateItem.focusX += pair.first
                }

                if (pair.second != 0F) {
                    stateItem.focusY += pair.second
                }

                // ReDraw
                invalidate()
            }
        }
    }

    inner class RotateListener : RotateGestureDetector.Companion.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            stateItem.rotationDegree -= detector.rotationsDegreesDelta
            return true
        }
    }
}