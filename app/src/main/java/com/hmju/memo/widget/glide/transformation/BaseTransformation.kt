package com.hmju.memo.widget.glide.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.IntDef
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

/**
 * Description : BaseBitmap Transformation
 *
 * Created by hmju on 2020-12-22
 */
abstract class BaseTransformation(@CornerType type: Int) : BitmapTransformation() {

    companion object {
        const val TOP_LEFT = 0x00000001
        const val TOP_RIGHT = 1 shl 1
        const val BOTTOM_LEFT = 1 shl 2
        const val BOTTOM_RIGHT = 1 shl 3
        const val ALL = 1 shl 4
    }

    @IntDef(flag = true, value = [TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, ALL])
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class CornerType

    @CornerType
    var mType = 0

    init {
        var _type = type
        if (type and TOP_LEFT == TOP_LEFT &&
            type and TOP_RIGHT == TOP_RIGHT &&
            type and BOTTOM_LEFT == BOTTOM_LEFT &&
            type and BOTTOM_RIGHT == BOTTOM_RIGHT) {
            _type = ALL
        }
        mType = _type
    }

    protected open fun setCanvasBitmapDensity(toTransform: Bitmap, canvasBitmap: Bitmap) {
        canvasBitmap.density = toTransform.density
    }

    // Avoids warnings in M+.
    protected open fun clear(canvas: Canvas) {
        canvas.setBitmap(null)
    }
}