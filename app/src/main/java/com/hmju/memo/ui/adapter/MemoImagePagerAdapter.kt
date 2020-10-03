package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemMemoDetailImageBinding
import com.hmju.memo.model.memo.FileItem

/**
 * Description : 메모 상세 페이지 이미지 PagerAdapter
 *
 * Created by juhongmin on 2020/09/06
 */
class MemoImagePagerAdapter(private val itemList: ArrayList<FileItem>) :
    RecyclerView.Adapter<MemoImagePagerAdapter.MemoDetailImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoDetailImageViewHolder {
        return MemoDetailImageViewHolder(
            parent = parent,
            layoutId = R.layout.item_memo_detail_image
        )
    }

    override fun onBindViewHolder(holder: MemoDetailImageViewHolder, pos: Int) {
        holder.binding.item = itemList[pos]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class MemoDetailImageViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemMemoDetailImageBinding>(parent, layoutId)
}