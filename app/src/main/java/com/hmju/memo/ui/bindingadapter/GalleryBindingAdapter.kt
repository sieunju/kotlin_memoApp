package com.hmju.memo.ui.bindingadapter

import android.database.Cursor
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.ui.adapter.GalleryAdapter
import com.hmju.memo.ui.adapter.GallerySelectedPhotoAdapter
import com.hmju.memo.ui.decoration.AlbumItemDecoration
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.GalleryViewModel

/**
 * Description : Album 및 Camera Binding Adapter
 *
 * Created by hmju on 2020-09-17
 */

@BindingAdapter(value = ["viewModel", "galleryCursor"])
fun setGalleryListAdapter(
    view: RecyclerView,
    viewModel: GalleryViewModel,
    cursor: Cursor?
) {
    view.adapter?.let { adapter ->
        cursor?.let {
            if (adapter is GalleryAdapter) {
                view.scrollToPosition(0)
                adapter.setCursor(it)
            }
        }
    } ?: run {
        GalleryAdapter(viewModel).apply {
            view.adapter = this
            view.layoutManager = GridLayoutManager(view.context, 3)
            view.addItemDecoration(
                AlbumItemDecoration(
                    spanCnt = 3,
                    divider = view.context.resources.getDimensionPixelSize(R.dimen.size_1)
                )
            )
            cursor?.let {
                view.scrollToPosition(0)
                this.setCursor(it)
            }
        }
    }
}

@BindingAdapter(value = ["viewModel", "selectedPhotoList"])
fun setSelectedPhotoListAdapter(
    view: RecyclerView,
    viewModel: GalleryViewModel,
    dataList: ArrayList<String>
) {
    view.adapter?.let { adapter ->
        if (adapter is GallerySelectedPhotoAdapter) {
            adapter.setDataList(dataList)
        }
    } ?: run {
        GallerySelectedPhotoAdapter(viewModel).apply {
            view.adapter = this
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)

        }
    }
}