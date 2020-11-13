package com.hmju.memo.ui.bindingadapter

import android.animation.ObjectAnimator
import android.database.Cursor
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.ui.adapter.GalleryAdapter
import com.hmju.memo.ui.adapter.GallerySelectedPhotoAdapter
import com.hmju.memo.ui.decoration.GalleryItemDecoration
import com.hmju.memo.ui.decoration.HorizontalItemDecoration
import com.hmju.memo.viewmodels.GalleryViewModel

/**
 * Gallery Contents Adapter
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
                GalleryItemDecoration(
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

@BindingAdapter(value = ["viewModel", "galleryFilterList"])
fun setGalleryFilterAdapter(
    view: RecyclerView,
    viewModel: GalleryViewModel,
    dataList: ArrayList<GalleryFilterItem>
) {
    view.adapter?.let {

    } ?: run {

    }
}

/**
 * Gallery Selected Photo Adapter
 */
@BindingAdapter(value = ["viewModel", "selectedPhotoList"])
fun setSelectedPhotoListAdapter(
    view: RecyclerView,
    viewModel: GalleryViewModel,
    dataList: ArrayList<String>
) {
    view.adapter?.let { adapter ->
        if (adapter is GallerySelectedPhotoAdapter) {
            adapter.setDataList(dataList)
            ObjectAnimator.ofFloat(view, View.ALPHA, 0.5F, 1F).apply {
                duration = 500
                start()
            }
            adapter.notifyDataSetChanged()

        }
    } ?: run {
        GallerySelectedPhotoAdapter(viewModel).apply {
            view.adapter = this
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            view.addItemDecoration(
                HorizontalItemDecoration(
                    side = view.resources.getDimensionPixelSize(R.dimen.size_10),
                    divider = view.resources.getDimensionPixelSize(R.dimen.size_2)
                )
            )

        }
    }
}