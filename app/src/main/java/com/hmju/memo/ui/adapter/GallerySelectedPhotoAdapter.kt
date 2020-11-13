package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGallerySelectedPhotoBinding
import com.hmju.memo.model.gallery.GallerySelectedItem
import com.hmju.memo.viewmodels.GalleryViewModel

/**
 * Description : 갤러리 선택한 리스트 관련 Adapter Class
 *
 * Created by juhongmin on 2020/09/28
 */
class GallerySelectedPhotoAdapter(
    private val viewModel: GalleryViewModel
) : RecyclerView.Adapter<GallerySelectedPhotoAdapter.ItemSelectedPhotoViewHolder>() {

    private val dataList = arrayListOf<String>()

    /**
     * setDataList
     * @param list -> 선택한 사진 리스트
     */
    fun setDataList(list: ArrayList<String>) {
        dataList.clear()
        dataList.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSelectedPhotoViewHolder {
        return ItemSelectedPhotoViewHolder(
            parent = parent,
            layoutId = R.layout.item_gallery_selected_photo,
            viewModel = viewModel
        )
    }

    override fun onBindViewHolder(holder: ItemSelectedPhotoViewHolder, pos: Int) {
        if(pos < dataList.size) {
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