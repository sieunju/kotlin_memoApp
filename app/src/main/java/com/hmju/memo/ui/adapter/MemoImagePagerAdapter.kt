package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemMemoDetailImageBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MemoDetailViewModel

/**
 * Description : 메모 상세 페이지 이미지 PagerAdapter
 *
 * Created by juhongmin on 2020/09/06
 */
class MemoImagePagerAdapter(private val itemList: ArrayList<String>) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MemoDetailImageViewHolder(
            parent = parent,
            layoutId = R.layout.item_memo_detail_image
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        if (holder is MemoDetailImageViewHolder) {
            holder.binding.imgUrl = itemList[pos]
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class MemoDetailImageViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemMemoDetailImageBinding>(parent, layoutId)
}