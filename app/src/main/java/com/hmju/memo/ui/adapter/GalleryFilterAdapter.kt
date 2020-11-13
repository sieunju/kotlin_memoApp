package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGalleryFilterBinding
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.viewmodels.GalleryViewModel

/**
 * Description : Gallery Filter Adapter Class
 *
 * Created by juhongmin on 2020/11/13
 */
class GalleryFilterAdapter(
    private val viewModel: GalleryViewModel,
    private val dataList: ArrayList<GalleryFilterItem>
) : RecyclerView.Adapter<GalleryFilterAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(parent, R.layout.item_gallery_filter, viewModel)

    override fun onBindViewHolder(holder: ItemViewHolder, pos: Int) {
        if (pos < dataList.size) {
            holder.binding.item = dataList[pos]
        }
    }

    override fun getItemCount() = dataList.size


    class ItemViewHolder(parent: ViewGroup, layoutId: Int, viewModel: GalleryViewModel) :
        BaseViewHolder<ItemGalleryFilterBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }
}