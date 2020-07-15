package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.hmju.memo.base.BasePagedAdapter
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemHorizontalLoadingBinding
import com.hmju.memo.databinding.ItemMemoImgBinding
import com.hmju.memo.databinding.ItemMemoNormalBinding
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.MainViewModel


/**
 * Description : 메모 리스트 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListAdapter(
    val viewModel: MainViewModel
) : BasePagedAdapter<MemoItem, BaseViewHolder<*>>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            R.layout.item_memo_normal -> {
                return MemoNormalViewHolder(
                    parent = parent,
                    layoutId = viewType,
                    viewModel = viewModel
                )
            }
            R.layout.item_memo_img -> {
                return MemoImgViewHolder(
                    parent = parent,
                    layoutId = viewType,
                    viewModel = viewModel
                )
            }
            else -> {
                return LoadingViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        when(holder){
            is MemoNormalViewHolder -> {
                holder.binding.item = getItem(pos)
            }
            is MemoImgViewHolder -> {
                holder.binding.item = getItem(pos)
            }
        }
    }

    override fun getItemViewType(pos: Int): Int {
        getItem(pos)?.let { item ->
            return if (item.isNormal) {
                R.layout.item_memo_normal
            } else {
                R.layout.item_memo_img
            }
        } ?: return R.layout.item_horizontal_loading
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemoItem>() {
            override fun areItemsTheSame(oldItem: MemoItem, newItem: MemoItem) =
                oldItem.manageNo == newItem.manageNo

            override fun areContentsTheSame(oldItem: MemoItem, newItem: MemoItem) =
                oldItem == newItem
        }
    }

    class MemoNormalViewHolder(parent: ViewGroup, layoutId: Int, viewModel: MainViewModel) :
        BaseViewHolder<ItemMemoNormalBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
            binding.listener = memoClick
        }
    }

    class MemoImgViewHolder(parent: ViewGroup, layoutId: Int, viewModel: MainViewModel) :
        BaseViewHolder<ItemMemoImgBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
            binding.listener = memoClick
        }
    }

    class LoadingViewHolder(parent: ViewGroup, layoutId: Int) :
            BaseViewHolder<ItemHorizontalLoadingBinding>(parent,layoutId)
}