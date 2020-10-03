package com.hmju.memo.widget.viewpagerIndicator.drawer

import android.graphics.Canvas
import com.hmju.memo.widget.viewpagerIndicator.option.IndicatorOptions

class RoundRectDrawer internal constructor(indicatorOptions: IndicatorOptions) : RectDrawer(indicatorOptions) {

    override fun drawRoundRect(canvas: Canvas, rx: Float, ry: Float) {
        canvas.drawRoundRect(mRectF, rx, ry, mPaint)
    }
}