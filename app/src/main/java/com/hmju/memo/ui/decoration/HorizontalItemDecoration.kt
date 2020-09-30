package com.hmju.memo.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.utils.JLogger

/**
 * Description: Horizontal RecyclerView Item Decoration
 *
 * Created by hmju on 2020-09-29
 */
class HorizontalItemDecoration(
    private var side: Int = 0,
    private var divider: Int= 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.getChildAdapterPosition(view)
        val cnt = state.itemCount

        outRect.right = divider

        // 좌 여백 있는 경우
        if(pos == 0) {
            outRect.left = side
        } else if(pos == cnt -1){
            // 맨 마지막
            outRect.right = side
        }
    }
}