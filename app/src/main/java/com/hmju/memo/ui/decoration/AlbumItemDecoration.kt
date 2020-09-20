package com.hmju.memo.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Description : 앨범 전용 Item Decoration
 *
 * Created by juhongmin on 2020/09/20
 */
class AlbumItemDecoration(
    private var spanCnt: Int,
    private var divider: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val column = pos % spanCnt

        outRect.left = column * divider / spanCnt
        outRect.right = divider - (column + 1) * divider / spanCnt

        // item Top
        if (pos >= spanCnt) {
            outRect.top = divider
        }
    }
}