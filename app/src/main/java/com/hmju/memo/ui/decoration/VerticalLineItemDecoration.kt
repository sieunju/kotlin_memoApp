package com.hmju.memo.ui.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Description : 구분선을 그릴수 있는 형태의 Vertical Item Decoration
 *
 * Created by juhongmin on 2020/11/14
 */
class VerticalLineItemDecoration(
    private val margin : Int,
    private val divider : Drawable
) : RecyclerView.ItemDecoration() {

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.save()
        val left = margin
        val right = parent.width - margin
        val childCnt = parent.childCount

        for(i in 0 until childCnt) {
            val child = parent.getChildAt(i)

            val pos = parent.getChildAdapterPosition(child)

            // 첫번째 포지션이거나 맨 마지막 인경우 패스
            if(pos == 0 || pos == state.itemCount - 1){
                continue
            }

            // Divider Rect + ChildView Height 의 좌표값 -> mBounds
            parent.getDecoratedBoundsWithMargins(child,mBounds)

            // 구분선은 위에서 칠한다.
            val top = mBounds.top
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left,top,right,bottom)
            divider.draw(c)
        }

        c.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)

        // onDraw 함수에 맞게 첫번쨰와 마지막 View 는 구분선을 그리지 않는다.
        if(pos == RecyclerView.NO_POSITION || pos == 0 || pos == state.itemCount - 1) {
            outRect.setEmpty()
        } else {
            // 구분선 위에서 칠하도록 함.
            outRect.set(0,divider.intrinsicHeight,0,0)
        }
    }
}