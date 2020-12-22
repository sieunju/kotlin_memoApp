package com.hmju.memo.widget.glide.transformation

import android.graphics.*
import android.os.Build
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

/**
 * Description :
 *
 * Created by hmju on 2020-12-22
 */
class BorderTransformation : BaseTransformation(ALL) {

    companion object {
        const val ID = "com.widget.glide.transformation.BorderTransformation"
    }

    var mBorderWidth = -1
    var mBorderColor = -1

    fun border(borderWidth: Int, borderColor: Int): BorderTransformation {
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
        // Draw Border
        drawBorder(toTransform)

        return toTransform
    }

    private fun drawBorder(toTransform: Bitmap) {
        if (mBorderWidth == -1) return

        val canvas = Canvas(toTransform)
        val path = Path()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mBorderColor
            style = Paint.Style.FILL
        }
        val rect = RectF()
        val right = toTransform.width
        val bottom = toTransform.height

        // Draw Inner Rect
        rect.set(
            mBorderWidth.toFloat(),
            mBorderWidth.toFloat(),
            (right - mBorderWidth).toFloat(),
            (bottom - mBorderWidth).toFloat()
        )

        path.addRect(rect, Path.Direction.CCW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.clipOutPath(path)
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        }

        // Draw Out Rect
        rect.set(0F, 0F, right.toFloat(), bottom.toFloat())
        path.rewind()
        path.addRect(
            rect,
            Path.Direction.CCW
        )
        canvas.drawPath(path, paint)
        clear(canvas)
    }

    override fun equals(other: Any?): Boolean {
        if (other is CornerTransformation) {
            return mType == other.mType &&
                    mBorderWidth == other.mBorderWidth &&
                    mBorderColor == other.mBorderColor
        }
        return false
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + mBorderWidth + mBorderColor).toByteArray(CHARSET))
    }

    override fun hashCode(): Int {
        var result = mBorderWidth
        result = 31 * result + mBorderColor
        return result
    }
}