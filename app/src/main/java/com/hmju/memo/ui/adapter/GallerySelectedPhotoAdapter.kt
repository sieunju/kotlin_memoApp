package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGallerySelectedPhotoBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.GalleryViewModel

/**
 * Description :
 *
 * Created by juhongmin on 2020/09/28
 */
class GallerySelectedPhotoAdapter(private val viewModel: GalleryViewModel) :
    RecyclerView.Adapter<GallerySelectedPhotoAdapter.ItemSelectedPhotoViewHolder>() {

    private val dataList = arrayListOf<String>()

    fun setDataList(list: ArrayList<String>) {
        JLogger.d("TEST:: Selected Photo ${list.size}")
        dataList.clear()
        dataList.addAll(list)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSelectedPhotoViewHolder {
        return ItemSelectedPhotoViewHolder(
            parent = parent,
            layoutId = R.layout.item_gallery_selected_photo,
            viewModel = viewModel
        )
    }

    override fun onBindViewHolder(holder: ItemSelectedPhotoViewHolder, pos: Int) {
        if (dataList.size > pos) {
            holder.binding.imgUrl = dataList[pos]
        }
    }

    override fun getItemCount() = dataList.size


    class ItemSelectedPhotoViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        viewModel: GalleryViewModel
    ) :
        BaseViewHolder<ItemGallerySelectedPhotoBinding>(
            parent = parent,
            layoutId = layoutId
        ) {
        init {
            binding.viewModel = viewModel
        }
    }
}