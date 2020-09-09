package com.hmju.memo.widget.blurView

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import com.hmju.memo.R

import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.hmju.memo.utils.JLogger


/**
 * Description : RealTime Blur View
 * Open Source Library 사용
 *
 * Created by juhongmin on 2020/08/29
 */
class BlurView(private val ctx: Context, private val attrs: AttributeSet) : View(ctx, attrs) {

    private var mDownSampleFactor: Float
    private var mOverlayColor: Int
    private var mBlurRadius: Float // default 10dp (0 < r <= 25)

    private val mBlurImpl by lazy { getBlurImpl() }
    private var mDirty = false
    private var mBitmapToBlur: Bitmap? = null
    private var mBlurredBitmap: Bitmap? = null
    private var mBlurringCanvas: Canvas? = null
    private var mIsRendering = false
    private val mPaint by lazy {
        Paint()
    }
    private val mRectSrc: Rect = Rect()
    private val mRectDst: Rect = Rect()

    // mDecorView should be the root view of the activity (even if you are on a different window like a dialog)
    private var mDecorView: View? = null

    // If the view is on different root view (usually means we are on a PopupWindow),
    // we need to manually call invalidate() in onPreDraw(), otherwise we will not be able to see the changes
    private var mDifferentRoot = false
    private var RENDERING_COUNT = 0
    private var BLUR_IMPL = 0

    init {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.BlurView)
        mBlurRadius = typedArray.getDimension(
            R.styleable.BlurView_blurRadius,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10F,
                ctx.resources.displayMetrics
            )
        )
        mDownSampleFactor = typedArray.getFloat(R.styleable.BlurView_downSampleFactor, 4F)
        mOverlayColor = typedArray.getColor(R.styleable.BlurView_overlayColor, 0xAAFFFFFF.toInt())
        typedArray.recycle()
    }

    private fun getBlurImpl(): BlurImpl {
        if (BLUR_IMPL == 0) {
            // try to use stock impl first
            try {
                val impl = AndroidStockBlurImpl()
                val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
                impl.prepare(context, bmp, 4F)
                impl.release()
                bmp.recycle()
                BLUR_IMPL = 3
            } catch (e: Throwable) {
            }
        }
        if (BLUR_IMPL == 0) {
            try {
                javaClass.classLoader!!.loadClass("androidx.renderscript.RenderScript")
                // initialize RenderScript to load jni impl
                // may throw unsatisfied link error
                val impl = AndroidXBlurImpl()
                val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
                impl.prepare(context, bmp, 4F)
                impl.release()
                bmp.recycle()
                BLUR_IMPL = 1
            } catch (e: Throwable) {
                // class not found or unsatisfied link
            }
        }
        if (BLUR_IMPL == 0) {
            // fallback to empty impl, which doesn't have blur effect
            BLUR_IMPL = -1
        }
        return when (BLUR_IMPL) {
            1 -> AndroidXBlurImpl()
            3 -> AndroidStockBlurImpl()
            else -> EmptyBlurImpl()
        }
    }

    fun setBlurRadius(radius: Float) {
        JLogger.d("setBlurRadius\t$radius")
        if (mBlurRadius != radius) {
            mBlurRadius = radius
            mDirty = true
            invalidate()
        }
    }

    fun setDownsampleFactor(factor: Float) {
        require(factor > 0) { "Downsample factor must be greater than 0." }
        if (mDownSampleFactor !== factor) {
            mDownSampleFactor = factor
            mDirty = true // may also change blur radius
            releaseBitmap()
            invalidate()
        }
    }

    fun setOverlayColor(color: Int) {
        if (mOverlayColor != color) {
            mOverlayColor = color
            invalidate()
        }
    }

    private fun releaseBitmap() {
        mBitmapToBlur?.recycle()
        mBitmapToBlur = null

        mBlurredBitmap?.recycle()
        mBlurredBitmap = null
    }

    private fun release() {
        releaseBitmap()
        mBlurImpl.release()
    }

    private fun prepare(): Boolean {
        if (mBlurRadius == 0f) {
            release()
            return false
        }
        var downsampleFactor: Float = mDownSampleFactor
        var radius = mBlurRadius / downsampleFactor
        if (radius > 25) {
            downsampleFactor = downsampleFactor * radius / 25
            radius = 25f
        }
        val width = width
        val height = height
        val scaledWidth = Math.max(1, (width / downsampleFactor).toInt())
        val scaledHeight = Math.max(1, (height / downsampleFactor).toInt())
        var dirty = mDirty
        if (mBlurringCanvas == null || mBlurredBitmap == null || mBlurredBitmap!!.width != scaledWidth || mBlurredBitmap!!.height != scaledHeight) {
            dirty = true
            releaseBitmap()
            var r = false
            try {
                mBitmapToBlur =
                    Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (mBitmapToBlur == null) {
                    return false
                }
                mBlurringCanvas = Canvas(mBitmapToBlur!!)
                mBlurredBitmap =
                    Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (mBlurredBitmap == null) {
                    return false
                }
                r = true
            } catch (e: OutOfMemoryError) {
                // Bitmap.createBitmap() may cause OOM error
                // Simply ignore and fallback
            } finally {
                if (!r) {
                    release()
                    return false
                }
            }
        }
        if (dirty) {
            mDirty = if (mBlurImpl.prepare(context, mBitmapToBlur, radius)) {
                false
            } else {
                return false
            }
        }
        return true
    }

    private fun blur(bitmapToBlur: Bitmap?, blurredBitmap: Bitmap?) {
        mBlurImpl.blur(bitmapToBlur, blurredBitmap)
    }

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        val locations = IntArray(2)
        var oldBmp = mBlurredBitmap
        val decor = mDecorView
        if (decor != null && isShown && prepare()) {
            val redrawBitmap = mBlurredBitmap != oldBmp
            oldBmp = null
            decor.getLocationOnScreen(locations)
            var x = -locations[0]
            var y = -locations[1]
            getLocationOnScreen(locations)
            x += locations[0]
            y += locations[1]

            // just erase transparent
            mBitmapToBlur!!.eraseColor(mOverlayColor and 0xffffff)
            val rc = mBlurringCanvas!!.save()
            mIsRendering = true
            RENDERING_COUNT++
            try {
                mBlurringCanvas!!.scale(
                    1f * mBitmapToBlur!!.width / width,
                    1f * mBitmapToBlur!!.height / height
                )
                mBlurringCanvas!!.translate(-x.toFloat(), -y.toFloat())
                if (decor.background != null) {
                    decor.background.draw(mBlurringCanvas!!)
                }
                decor.draw(mBlurringCanvas)
            } catch (e: StopException) {
            } finally {
                mIsRendering = false
                RENDERING_COUNT--
                mBlurringCanvas!!.restoreToCount(rc)
            }
            blur(mBitmapToBlur, mBlurredBitmap)
            if (redrawBitmap || mDifferentRoot) {
                invalidate()
            }
        }
        true
    }

    protected fun getActivityDecorView(): View? {
        var ctx = context
        var i = 0
        while (i < 4 && ctx != null && ctx !is Activity && ctx is ContextWrapper) {
            ctx = ctx.baseContext
            i++
        }
        return if (ctx is Activity) {
            ctx.window.decorView
        } else {
            null
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mDecorView = getActivityDecorView()
        mDecorView?.let {
            it.viewTreeObserver.addOnPreDrawListener(preDrawListener)
            mDifferentRoot = it.rootView !== rootView
            if (mDifferentRoot) {
                it.postInvalidate()
            }
        } ?: run {
            mDifferentRoot = false
        }
    }

    override fun onDetachedFromWindow() {
        mDecorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
        release()
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas?) {
        if (mIsRendering) {
            // Quit here, don't draw views above me
            throw STOP_EXCEPTION
        } else if (RENDERING_COUNT > 0) {
            // Doesn't support blurview overlap on another blurview
        } else {
            super.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBlurredBitmap(canvas, mBlurredBitmap, mOverlayColor)
    }

    /**
     * Custom draw the blurred bitmap and color to define your own shape
     *
     * @param canvas
     * @param blurredBitmap
     * @param overlayColor
     */
    protected fun drawBlurredBitmap(canvas: Canvas, blurredBitmap: Bitmap?, overlayColor: Int) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.width
            mRectSrc.bottom = blurredBitmap.height
            mRectDst.right = width
            mRectDst.bottom = height
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null)
        }
        mPaint!!.color = overlayColor
        canvas.drawRect(mRectDst, mPaint)
    }

    private class StopException : RuntimeException()

    private val STOP_EXCEPTION = StopException()

    interface BlurImpl {
        fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean

        fun release()

        fun blur(input: Bitmap?, output: Bitmap?)
    }

    inner class AndroidStockBlurImpl : BlurImpl {
        private var mRenderScript: RenderScript? = null
        private var mBlurScript: ScriptIntrinsicBlur? = null
        private var mBlurInput: Allocation? = null
        private var mBlurOutput: Allocation? = null

        override fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean {
            if (mRenderScript == null) {
                try {
                    mRenderScript = RenderScript.create(context)
                    mBlurScript =
                        ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript))
                } catch (e: android.renderscript.RSRuntimeException) {
                    return if (isDebug(context)) {
                        throw e
                    } else {
                        // In release mode, just ignore
                        release()
                        false
                    }
                }
            }
            mBlurScript!!.setRadius(radius)
            mBlurInput = Allocation.createFromBitmap(
                mRenderScript, buffer,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT
            )
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput!!.getType())
            return true
        }

        override fun release() {
            mBlurInput?.destroy()
            mBlurInput = null

            mBlurOutput?.destroy()
            mBlurOutput = null

            mBlurScript?.destroy()
            mBlurScript = null

            mRenderScript?.destroy()
            mRenderScript = null
        }

        override fun blur(input: Bitmap?, output: Bitmap?) {
            mBlurInput?.copyFrom(input)
            mBlurScript?.setInput(mBlurInput)
            mBlurScript?.forEach(mBlurOutput)
            mBlurOutput?.copyTo(output)
        }

        // android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
        var DEBUG: Boolean? = null

        private fun isDebug(ctx: Context?): Boolean {
            if (DEBUG == null && ctx != null) {
                DEBUG = ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            }
            return DEBUG === java.lang.Boolean.TRUE
        }
    }

    inner class AndroidXBlurImpl : BlurImpl {
        private var mRenderScript: RenderScript? = null
        private var mBlurScript: ScriptIntrinsicBlur? = null
        private var mBlurInput: Allocation? = null
        private var mBlurOutput: Allocation? = null

        override fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean {
            if (mRenderScript == null) {
                try {
                    mRenderScript = RenderScript.create(context)
                    mBlurScript =
                        ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript))
                } catch (e: android.renderscript.RSRuntimeException) {
                    return if (isDebug(context)) {
                        throw e
                    } else {
                        // In release mode, just ignore
                        release()
                        false
                    }
                }
            }
            mBlurScript!!.setRadius(radius)
            mBlurInput = Allocation.createFromBitmap(
                mRenderScript, buffer,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT
            )
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput!!.type)
            return true
        }

        override fun release() {
            mBlurInput?.destroy()
            mBlurInput = null

            mBlurInput?.destroy()
            mBlurInput = null

            mBlurOutput?.destroy()
            mBlurOutput = null

            mBlurScript?.destroy()
            mBlurScript = null

            mRenderScript?.destroy()
            mRenderScript = null
        }

        override fun blur(input: Bitmap?, output: Bitmap?) {
            mBlurInput?.copyFrom(input)

            mBlurScript?.setInput(mBlurInput)
            mBlurScript?.forEach(mBlurOutput)

            mBlurOutput?.copyTo(output)
        }

        // android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
        var DEBUG: Boolean? = null

        fun isDebug(ctx: Context?): Boolean {
            if (DEBUG == null && ctx != null) {
                DEBUG = ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            }
            return DEBUG === java.lang.Boolean.TRUE
        }
    }

    inner class EmptyBlurImpl : BlurImpl {
        override fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean {
            return false
        }

        override fun release() {}
        override fun blur(input: Bitmap?, output: Bitmap?) {}
    }
}