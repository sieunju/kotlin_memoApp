package com.hmju.memo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.databinding.ItemMemoImgBinding
import com.hmju.memo.databinding.ItemMemoNormalBinding
import com.hmju.memo.model.memo.MemoImgItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoNormaItem
import com.hmju.memo.viewModels.MainViewModel

/**
 * Description : 메모 리스트 Adpater
 * 추후 BaseAdapter 를 상속 하도록 작업 예정
 *
 * Created by hmju on 2020-06-12
 */
class MemoListAdapter(
    private val viewModel: MainViewModel,
    private val dataList: ListMutableLiveData<Companion.ItemStruct<*>>
) : BaseAdapter(dataList) {

    override fun onDataChanged() {
        super.setSummitList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            TYPE_MEMO_NORMAL -> {
                return MemoNormalViewHolder(
                    parent = parent,
                    layoutId = R.layout.item_memo_normal,
                    viewModel = viewModel
                )
            }
            TYPE_MEMO_IMG -> {
                return MemoImgViewHolder(
                    parent = parent,
                    layoutId = R.layout.item_memo_img,
                    viewModel = viewModel
                )
            }
            else -> throw IllegalArgumentException("")
        }
    }

    @SuppressWarnings("unchecked")
    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        when (holder) {
            is MemoNormalViewHolder -> {
                holder.binding.item = mItems.get(pos).data as MemoNormaItem
            }
            is MemoImgViewHolder -> {
                holder.binding.item = mItems.get(pos).data as MemoImgItem
            }
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
}