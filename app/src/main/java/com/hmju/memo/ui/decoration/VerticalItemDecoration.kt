package com.hmju.memo.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.utils.JLogger

/**
 * Description : Vertical RecyclerView ItemDecoration
 *
 * Created by juhongmin on 2020/08/30
 */
class VerticalItemDecoration(
    private var side: Int = 0,
    private var divider: Int = 0,
    private var bottomSpace: Int = 0
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

        if (side != 0) {
            outRect.left = side
            outRect.right = side
        }

        if (divider != 0) {
            outRect.top = divider
        }

        if (pos == cnt - 1 && bottomSpace != 0) {
            outRect.bottom = bottomSpace
        }
    }
}