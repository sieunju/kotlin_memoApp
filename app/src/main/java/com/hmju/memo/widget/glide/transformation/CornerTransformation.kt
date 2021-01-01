package com.hmju.memo.widget.glide.transformation

import android.graphics.*
import android.os.Build
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

/**
 * Description : Glide Corner And Border Class
 *
 * Created by hmju on 2020-12-22
 */
class CornerTransformation(@CornerType type: Int) : BaseTransformation(type) {

    companion object {
        const val ID = "com.hmju.memo.widget.glide.transformation.CornerTransformation"
    }

    var mCornerRadius = 0
    var mBorderWidth = -1
    var mBorderColor = -1

    fun corner(cornerRadius: Int): CornerTransformation {
        mCornerRadius = cornerRadius
        return this
    }

    fun border(borderWidth: Int, borderColor: Int): CornerTransformation {
        mBorderWidth = borderWidth
        mBorderColor = borderColor
        return this
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val bitmap =
            pool.get(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888).apply {
                setHasAlpha(true)
            }

        setCanvasBitmapDensity(toTransform, bitmap)

        // Draw Round Rect
        drawRoundRect(bitmap, toTransform)

        // Draw Border
        drawBorder(bitmap)

        return bitmap
    }

    /**
     * Draw Round Rect Func..
     *
     * @param bitmap      Current Bitmap
     * @param toTransform Source Bitmap
     */
    private fun drawRoundRect(bitmap: Bitmap, toTransform: Bitmap) {
        if (mCornerRadius == 0) return

        val right = toTransform.width
        val bottom = toTransform.height

        val canvas = Canvas(bitmap).apply {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val rect = RectF()

        // Type All
        if ((mType and ALL) == ALL) {
            rect.set(0F, 0F, right.toFloat(), bottom.toFloat())
            canvas.drawRoundRect(rect, mCornerRadius.toFloat(), mCornerRadius.toFloat(), paint)
        } else {
            val diameter = mCornerRadius * 2 // 기울기 길이

            if (mType and TOP_LEFT == TOP_LEFT) {
                rect.set(0F, 0F, diameter.toFloat(), diameter.toFloat())
                canvas.drawArc(rect, 100F, 90F, true, paint) // ┌─
            } else {
                rect.set(0F, 0F, mCornerRadius.toFloat(), mCornerRadius.toFloat())
                canvas.drawRect(rect, paint) // Top Left ■
            }

            if (mType and TOP_RIGHT == TOP_RIGHT) {
                rect.set((right - diameter).toFloat(), 0F, right.toFloat(), diameter.toFloat())
                canvas.drawArc(rect, 270F, 90F, true, paint) // ─┐
            } else {
                rect.set(
                    (right - mCornerRadius).toFloat(),
                    0F,
                    right.toFloat(),
                    mCornerRadius.toFloat()
                )
                canvas.drawRect(rect, paint) // Top Right ■
            }

            rect.set(
                mCornerRadius.toFloat(),
                0F,
                (right - mCornerRadius).toFloat(),
                (diameter / 2).toFloat()
            )
            canvas.drawRect(rect, paint) // Top ──

            if (mType and BOTTOM_LEFT == BOTTOM_LEFT) {
                rect.set(0F, (bottom - diameter).toFloat(), diameter.toFloat(), bottom.toFloat())
                canvas.drawArc(rect, 90F, 90F, true, paint) // └─
            } else {
                rect.set(
                    0F,
                    (bottom - mCornerRadius).toFloat(),
                    mCornerRadius.toFloat(),
                    bottom.toFloat()
                )
                canvas.drawRect(rect, paint)  // Bottom Left ■
            }

            if (mType and BOTTOM_RIGHT == BOTTOM_RIGHT) {
                rect.set(
                    (right - diameter).toFloat(),
                    (bottom - diameter).toFloat(),
                    right.toFloat(),
                    bottom.toFloat()
                )
                canvas.drawArc(rect, 0F, 90F, true, paint) // ─┘
            } else {
                rect.set(
                    (right - mCornerRadius).toFloat(),
                    (bottom - mCornerRadius).toFloat(),
                    right.toFloat(),
                    bottom.toFloat()
                )
                canvas.drawRect(rect, paint) // Bottom Right ■
            }

            rect.set(
                mCornerRadius.toFloat(),
                (bottom - mCornerRadius).toFloat(),
                (right - mCornerRadius).toFloat(),
                bottom.toFloat()
            )
            canvas.drawRect(rect, paint) // Bottom ──

            // Body Rect
            rect.set(
                0F,
                mCornerRadius.toFloat(),
                right.toFloat(),
                (bottom - mCornerRadius).toFloat()
            )
            canvas.drawRect(rect, paint)// Body Rect
        }

        clear(canvas)
    }

    /**
     * Draw Border
     *
     * @param toTransform Source Bitmap
     */
    private fun drawBorder(toTransform: Bitmap) {
        if (mBorderWidth == -1) return

        val canvas = Canvas(toTransform)
        val path = Path()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mBorderColor
        }

        val rect = RectF()
        val right = toTransform.width
        val bottom = toTransform.height

        // Type ALl
        if (mType and ALL == ALL) {
            paint.style = Paint.Style.FILL

            // Draw Inner Rect
            rect.set(
                mBorderWidth.toFloat(),
                mBorderWidth.toFloat(),
                (right - mBorderWidth).toFloat(),
                (bottom - mBorderWidth).toFloat()
            )
            val innerRadius = mCornerRadius - mBorderWidth / 2F
            path.addRoundRect(
                rect,
                innerRadius,
                innerRadius,
                Path.Direction.CCW
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipOutPath(path)
            } else {
                canvas.clipPath(path, Region.Op.DIFFERENCE)
            }

            // Draw Out Rect
            rect.set(0F, 0F, right.toFloat(), bottom.toFloat())
            path.rewind()
            path.addRoundRect(
                rect,
                mCornerRadius.toFloat(),
                mCornerRadius.toFloat(),
                Path.Direction.CCW
            )
        } else {
            // Other Type.
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = mBorderWidth.toFloat()
            val lineMiddle = mBorderWidth / 2F

            // Start Path
            path.moveTo(lineMiddle, mCornerRadius.toFloat())

            // Round Top Left
            if (mType and TOP_LEFT == TOP_LEFT) {
                path.quadTo(
                    lineMiddle,
                    lineMiddle,
                    mCornerRadius.toFloat(),
                    lineMiddle
                )
            } else {
                path.lineTo(lineMiddle, lineMiddle)
                path.lineTo(mCornerRadius.toFloat(), lineMiddle)
            }

            // Top Line
            path.lineTo((right - mCornerRadius).toFloat(), lineMiddle)

            // Round Top Right
            if (mType and TOP_RIGHT == TOP_RIGHT) {
                path.quadTo(
                    right - lineMiddle,
                    lineMiddle,
                    right - lineMiddle,
                    mCornerRadius.toFloat()
                )
            } else {
                path.lineTo(right - lineMiddle, lineMiddle)
                path.lineTo(right - lineMiddle, mCornerRadius.toFloat())
            }

            // Right Line
            path.lineTo(right - lineMiddle, (bottom - mCornerRadius).toFloat())

            // Round Bottom Right
            if (mType and BOTTOM_RIGHT == BOTTOM_RIGHT) {
                path.quadTo(
                    right - lineMiddle,
                    bottom - lineMiddle,
                    (right - mCornerRadius).toFloat(),
                    bottom - lineMiddle
                )
            } else {
                path.lineTo(right - lineMiddle, bottom - lineMiddle)
                path.lineTo((right - mCornerRadius).toFloat(), bottom - lineMiddle)
            }

            // Bottom Line
            path.lineTo(mCornerRadius.toFloat(), bottom - lineMiddle)

            // Round Bottom Left
            if (mType and BOTTOM_LEFT == BOTTOM_LEFT) {
                path.quadTo(
                    lineMiddle,
                    bottom - lineMiddle,
                    lineMiddle,
                    (bottom - mCornerRadius).toFloat()
                )
            } else {
                path.lineTo(lineMiddle, bottom - lineMiddle)
                path.lineTo(lineMiddle, (bottom - mCornerRadius).toFloat())
            }

            // Left Line
            path.lineTo(lineMiddle, mCornerRadius.toFloat())
            path.close()
        }
        canvas.drawPath(path, paint)
        clear(canvas)
    }

    override fun equals(other: Any?): Boolean {
        if (other is CornerTransformation) {
            return mType == other.mType &&
                    mCornerRadius == other.mCornerRadius &&
                    mBorderWidth == other.mBorderWidth &&
                    mBorderColor == other.mBorderColor
        }
        return false
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + mCornerRadius + mBorderWidth + mBorderColor).toByteArray(CHARSET))
    }

    override fun hashCode(): Int {
        var result = mCornerRadius
        result = 31 * result + mBorderWidth
        result = 31 * result + mBorderColor
        return result
    }
}