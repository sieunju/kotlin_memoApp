package com.hmju.memo.widget.flexibleImageView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.*
import com.hmju.memo.BuildConfig
import com.hmju.memo.utils.JLogger
import com.hmju.memo.widget.flexibleImageView.gesture.MoveGestureDetector
import kotlin.math.*

/**
 * Description :
 *
 * Created by hmju on 2021-03-04
 */
class FlexibleWebView @JvmOverloads constructor(
        private val ctx: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : WebView(ctx, attrs, defStyleAttr), LifecycleOwner, LifecycleObserver {

    interface Listener {
        fun onChange()
    }

    /**
     * Description: Flexible State Data Class
     * 각 초기값은
     * Scale 1.0F
     * Focus 0F
     * Rotate 0F
     * Flip 1F
     * Created by hmju on 2020-10-08
     */
    data class FlexibleStateItem(
            var scale: Float,
            var focusX: Float,
            var focusY: Float,
            var rotationDegree: Float,
            var flipX: Float,
            var flipY: Float
    ) {

        val scaleX: Float
            get() = scale * flipX
        val scaleY: Float
            get() = scale * flipY
        var minScale: Float = 1F
    }

    companion object {
        const val MAX_LONG_CLICK_DISTANCE = 16
        const val MAX_SCALE_FACTOR = 3.0F
        const val MIN_SCALE_FACTOR = 1.0F
    }

    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(ctx, ScaleListener())
    }
    private val moveGestureDetector: MoveGestureDetector by lazy {
        MoveGestureDetector(ctx, MoveListener())
    }

    var stateItem = FlexibleStateItem(
            scale = 1.0F,
            focusX = 0F,
            focusY = 0F,
            rotationDegree = 0F,
            flipX = 1F,
            flipY = 1F
    )
    var changeListener: Listener? = null
    private var isMultiTouch: Boolean = false
    private var touchPoint = PointF()
    private var moveDistance: Double = 0.0
    private var webViewHeight = -1
    val disallowInterceptor = MutableLiveData<Boolean>() // Parent Touch Disable.

    init {
        if (!isInEditMode) {
            setWebSetting()
        }
    }

    fun reset() {
        moveDistance = 0.0
        stateItem.scale = 1F
        stateItem.focusY = 0F
        stateItem.focusX = 0F
        invalidate()
    }

    fun loadData(data: String) {
        loadDataWithBaseURL(
                "NetworkDefine.BASE_URL",
                data,
                "text/html",
                "UTF-8",
                null
        )
        addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, _ ->
            webViewHeight = bottom
            this.requestLayout()
        }
    }

    /**
     * Setting WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSetting() {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        settings.apply {
            javaScriptEnabled = true // 자바 스크립트 허용
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // http 리소스 허용
            loadsImagesAutomatically = true // 앱 이미지 리소스 자동 로드
            if (BuildConfig.IS_DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStateEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    override fun getLifecycle() = lifecycleRegistry

    @SuppressLint("Recycle")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!isEnabled || ev == null) return false
//        if (ev.pointerCount > 1) {
        disallowInterceptor.value = true
        // compute trans from
        val prop = arrayOfNulls<MotionEvent.PointerProperties>(ev.pointerCount)
        val cords = arrayOfNulls<MotionEvent.PointerCoords>(ev.pointerCount)

        // get First Coords
        ev.getPointerCoords(0, MotionEvent.PointerCoords())

        for (i in 0 until ev.pointerCount) {
            val properties = MotionEvent.PointerProperties()
            ev.getPointerProperties(i, properties)
            prop[i] = properties

            val cod = MotionEvent.PointerCoords()
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
        moveGestureDetector.onTouchEvent(baseMotionEvent)
//        } else {
//            disallowInterceptor.value = false
//        }

        computeClickEvent(ev)

        invalidate()
        super.onTouchEvent(ev)

        return true
    }

    override fun onDraw(canvas: Canvas?) {
        translationX = stateItem.focusX
        translationY = stateItem.focusY
        scaleX = stateItem.scaleX
        scaleY = stateItem.scaleY
        JLogger.d("onDraw ${stateItem.scale}")
        super.onDraw(canvas)
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

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        var prevScale = 0.5F

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = stateItem.scale * detector.scaleFactor

            prevScale = scale

            // 범위 를 넘어 가는 경우 false 리턴.
            if (scale <= FlexibleWebView.MIN_SCALE_FACTOR || scale >= FlexibleWebView.MAX_SCALE_FACTOR) {
                return false
            }

            stateItem.scale = scale
            changeListener?.onChange()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            // 이미지 확대 축소 제한
//            if (prevScale < stateItem.minScale) {
//                stateItem.scale = stateItem.minScale
//                onWebViewScaled()
//            }
//
//            Logger.d("onScaleEnd")
//            if (isWebViewInit != null && isWebViewInit?.value == true) {
//                isWebViewInit?.value = false
//            }
        }
    }

    inner class MoveListener : MoveGestureDetector.OnMoveGestureListener {

        override fun onMoveBegin(detector: MoveGestureDetector): Boolean {
            return true
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            val delta = detector.currentFocus
            stateItem.focusX += delta.x
            stateItem.focusY += delta.y
            changeListener?.onChange()
            return true
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
        }
    }

}