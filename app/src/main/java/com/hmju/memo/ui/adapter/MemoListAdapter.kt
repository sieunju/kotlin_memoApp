package com.hmju.memo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.databinding.ItemMemoImgBinding
import com.hmju.memo.databinding.ItemMemoNormalBinding
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.MainViewModel

/**
 * Description : 메모 리스트 Adpater
 * 추후 BaseAdapter 를 상속 하도록 작업 예정
 *
 * Created by hmju on 2020-06-12
 */
class MemoListAdapter(
    private val viewModel: MainViewModel,
    private val dataList: ListMutableLiveData<MemoItem>
) : BaseAdapter() {

    override fun onDataChanged() {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MemoNormalViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_memo_normal, parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        when (holder) {
            is MemoNormalViewHolder -> {
                holder.binding.item = dataList.get(pos)
            }
        }
    }

    class MemoNormalViewHolder(view: View, viewModel: MainViewModel) :
        BaseViewHolder<ItemMemoNormalBinding>(view) {
        init {
            binding.viewModel = viewModel
            binding.listener = memoClick
        }
    }

//    class MemoImgViewHolder(view: View, viewModel: MainViewModel) :
//        BaseViewHolder<ItemMemoImgBinding>(view) {
//        init {
//        }
//    }
}