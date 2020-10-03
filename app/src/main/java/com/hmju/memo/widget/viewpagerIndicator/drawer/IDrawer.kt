package com.hmju.memo.widget.viewpagerIndicator.drawer

import android.graphics.Canvas

interface IDrawer {

    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): BaseDrawer.MeasureResult

    fun onDraw(canvas: Canvas)
}