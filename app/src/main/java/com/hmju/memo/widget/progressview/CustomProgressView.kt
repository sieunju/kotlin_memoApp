package com.hmju.memo.widget.progressview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.hmju.memo.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * kotlin_github
 * Class: CustomProgressView
 * Created by hmju on 2019-01-27.
 * └ Revised by hmju on 2019-09-16.
 *
 * Description:  실시간 진행률을 바로 보여주게 하는 SurfaceView 기반의
 * ProgressBar Class
 */
class CustomProgressView : SurfaceView, SurfaceHolder.Callback {

    private val TAG: String = "JLogger"

    private val mHolder: SurfaceHolder by lazy { holder }

    // [s] Attributes Value Define
    enum class TYPE {
        HORIZONTAL, VERTICAL
    }

    // 프로그래스 색상 및 라운딩 처리에 대한 정보 구조체.
    internal class GradientInfo {
        var radius: Int = 0
        var startColor: Int = 0
        var centerColor: Int = 0
        var endColor: Int = 0
        var location: Float = 0F

    }

    internal val mGradientInfo: GradientInfo = GradientInfo()
    internal var mType: TYPE = TYPE.HORIZONTAL  // default horizontal
    internal var mMax: Int = 100                // default Value 100
    internal var mBgColor: Int = Color.BLACK    // default Black
    // [e] Attributes Value Define

    private var mThread: ProgressThread? = null
    private var mCurrentProgress: Int = 0

    // Surface Life Cycle
    // Type 은 2가지로 표현 한다. 더 필요한 경우 타입 추가.
    internal enum class LIFE {
        CAN_DRAW, NOT_DRAW
    }

    private var mCurrentLife: LIFE = LIFE.NOT_DRAW

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        // default Setting
        mHolder.addCallback(this)
        initView(attrs)
    }

    /**
     * init View.
     * @param attrs 셋팅한 속성 값.
     * @author hmju
     */
    private fun initView(attrs: AttributeSet?) {
        if (isInEditMode) return

        // 기본 SurfaceView 투명화
        setZOrderOnTop(true)
        mHolder.setFormat(PixelFormat.TRANSPARENT)

        // 속성값 세팅.
        if (attrs != null) {
            val attr: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressView)
            mBgColor = attr.getColor(R.styleable.CustomProgressView_bgColor, Color.BLACK)
            mType = TYPE.values()[attr.getInt(R.styleable.CustomProgressView_type, 0)]
            mMax = attr.getInt(R.styleable.CustomProgressView_max, 100)
            // set Start Progress
            mCurrentProgress = attr.getInt(R.styleable.CustomProgressView_min, 0)

            mGradientInfo.radius = attr.getDimensionPixelOffset(R.styleable.CustomProgressView_gradientRadius, 0)
            mGradientInfo.startColor = attr.getColor(R.styleable.CustomProgressView_gradientStartColor, Color.BLACK)
            mGradientInfo.centerColor = attr.getColor(R.styleable.CustomProgressView_gradientCenterColor, Color.BLACK)
            mGradientInfo.endColor = attr.getColor(R.styleable.CustomProgressView_gradientEndColor, Color.BLACK)
            mGradientInfo.location = attr.getFloat(R.styleable.CustomProgressView_gradientLocation, 0F)

            attr.recycle()
        }
    }

    fun setType(type: TYPE): CustomProgressView {
        mType = type
        return this
    }

    fun setMin(min: Int): CustomProgressView {
        mCurrentProgress = min
        return this
    }

    fun setBgColor(id: Int): CustomProgressView {
        mBgColor = id
        return this
    }

    fun setRadius(radius: Int): CustomProgressView {
        mGradientInfo.radius = radius
        return this
    }

    fun setStartColor(color: Int): CustomProgressView {
        mGradientInfo.startColor = color
        return this
    }

    fun setCenterColor(color: Int): CustomProgressView {
        mGradientInfo.centerColor = color
        return this
    }

    fun setEndColor(color: Int): CustomProgressView {
        mGradientInfo.endColor = color
        return this
    }

    fun setGradientLocation(location: Float): CustomProgressView {
        mGradientInfo.location = location
        return this
    }

    /**
     * 프로그래스 진행률을 노출하는 함수.
     * {@link Reference #ProgressBar.setProgress(int)}
     * @param progress 현재 진행도를 나타내고 싶은 수치
     * @author hmju
     */
    fun setProgress(progress: Int) {
        mCurrentProgress = progress
        if (mCurrentLife == LIFE.CAN_DRAW) {
            mThread?.draw()
        }
    }

    /**
     * 프로그래스 증가율을 노출하는 함수.
     * {@link Reference #ProgressBar.incrementProgressBy(int}
     * @param diff 증가율
     * @author hmju
     */
    fun incrementProgressBy(diff: Int) {
        mCurrentProgress += diff
        if (mCurrentLife == LIFE.CAN_DRAW) {
            mThread?.draw()
        }
    }

    /**
     * get Current Progress
     * @author hmju
     */
    fun getProgress(): Int {
        return mCurrentProgress
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            mThread?.closeThread()
            mThread = null
        }

        // init Thread.
        mCurrentLife = LIFE.CAN_DRAW
        mThread = ProgressThread(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCurrentLife = LIFE.NOT_DRAW
        mThread?.closeThread()
    }

    /**
     * ProgressThread Class
     *
     * @author hmju
     */
    internal inner class ProgressThread(private val width: Int, private val height: Int) : Runnable {
        private val thread: ExecutorService = Executors.newFixedThreadPool(1)
        private val fgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG) // 부드럽게 처리하는 Flag
        private val bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)  // 부드럽게 처리하는 Flag

        //        private val txtPaint: Paint = Paint()
        private val bgRectF: RectF = RectF(0F, 0F, width.toFloat(), height.toFloat())
        private val radius: Float = mGradientInfo.radius.toFloat()
        private val fgRect: Rect

        init {

//            txtPaint.color = Color.BLACK
//            txtPaint.textSize = 30F

            // [s] init Background
            bgPaint.color = mBgColor
            // [e] init Background

            // [s] init Gradient
            val gradient: LinearGradient
            // 라운딩 처리된 배경에 그라데이트되어 있는 Foreground 를 입힌다.
            fgPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
            // Type 별 초기화
            if (mType == TYPE.HORIZONTAL) {
                fgRect = Rect(0, 0, 0, height)
                gradient = LinearGradient(
                        0F, 0F, 0F, height.toFloat(),
                        intArrayOf(
                                mGradientInfo.startColor,
                                mGradientInfo.centerColor,
                                mGradientInfo.endColor
                        ),
                        floatArrayOf(0F, mGradientInfo.location, 1F), Shader.TileMode.CLAMP
                )
            }
            // Vertical
            else {
                fgRect = Rect(0, height, width, height)
                gradient = LinearGradient(
                        0F, 0F, 0F, height.toFloat(),
                        intArrayOf(
                                mGradientInfo.startColor,
                                mGradientInfo.centerColor,
                                mGradientInfo.endColor
                        ),
                        floatArrayOf(0F, mGradientInfo.location, 1F), Shader.TileMode.CLAMP
                )
            }

            // set Paint Shader
            fgPaint.shader = gradient
            // [e] init Gradient

            // [s] init Thread
            thread.execute(this)
            // setBgColor
            draw()
            // [e] init Thread
        }

        /**
         * Run Draw.
         * @author hmju
         */
        override fun run() {
            val canvas: Canvas? = mHolder.lockCanvas()
            try {
                synchronized(mHolder) {
                    // draw background
                    canvas?.drawRoundRect(bgRectF, radius, radius, bgPaint)

                    // 초기값인 경우 아래 로직 패스.
                    if (mCurrentProgress == 0) {
                        return@run
                    }

                    // Type 별로 분기처리
                    if (mType == TYPE.HORIZONTAL) {
                        val updateValue: Float = (width * (mCurrentProgress.toFloat() / mMax.toFloat()))
                        fgRect.right = updateValue.toInt()
                    }
                    // Type Vertical
                    else {
                        val updateValue: Float = (((mMax - mCurrentProgress) * height) / mMax).toFloat()
                        fgRect.top = updateValue.toInt()
                    }

                    // draw ForeGround
                    return@synchronized canvas?.drawRect(fgRect, fgPaint)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Thread Error\t${ex.message}")
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas)
                }
            }
        }

        /**
         * Run Draw
         * @author hmmu
         */
        internal fun draw() {
            try {
                this.run()
            } catch (ex: Exception) {
                Log.e(TAG, "Draw Error\t${ex.message}")
            }
        }

        /**
         * Call ShutDown Thread
         * @author hmju
         */
        internal fun closeThread() {
            try {
                thread.shutdownNow()
            } catch (ex: Exception) {
                Log.e(TAG, "CloseThread Error\t${ex.message}")
            }
        }
    }
}