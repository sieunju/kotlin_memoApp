package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGalleryFilterBinding
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.ui.gallery.SelectedFilterBottomSheet

/**
 * Description : Gallery Filter Adapter Class
 * 앨범 선택창 Adapter
 * Created by juhongmin on 2020/11/13
 */
class GalleryFilterAdapter(
    private val listener: SelectedFilterBottomSheet.Listener
) : RecyclerView.Adapter<GalleryFilterAdapter.ItemViewHolder>() {

    private val dataList = arrayListOf<GalleryFilterItem>()

    fun setDataList(list: ArrayList<GalleryFilterItem>) {
        dataList.clear()
        dataList.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(parent, R.layout.item_gallery_filter, listener)

    override fun onBindViewHolder(holder: ItemViewHolder, pos: Int) {
        if (pos < dataList.size) {
            holder.binding.item = dataList[pos]
        }
    }

    override fun getItemCount() = dataList.size

    class ItemViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        listener: SelectedFilterBottomSheet.Listener
    ) :
        BaseViewHolder<ItemGalleryFilterBinding>(parent, layoutId) {
        init {
            binding.listener = listener
        }
    }
}