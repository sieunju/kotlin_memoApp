package com.hmju.memo.ui.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hmju.memo.R
import com.hmju.memo.define.TagType
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.ui.decoration.VerticalItemDecoration
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel

/**
 * Description : 메모 리스트 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter(value = ["viewModel", "memoList"])
fun setMemoListAdapter(
    view: RecyclerView,
    viewModel: MainViewModel,
    memoList: PagedList<MemoItem>?
) {
    view.adapter?.let { adapter ->

        if (adapter is MemoListAdapter) {
            memoList?.let{pagedList->
                if(adapter.itemCount > pagedList.loadedCount) {
                    adapter.notifyItemRangeRemoved(0,adapter.itemCount)
                }
                adapter.submitList(pagedList)
            }
        }
    } ?: run {
        MemoListAdapter(viewModel).apply {
            view.adapter = this
            view.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            view.addItemDecoration(
                VerticalItemDecoration(
                    divider = view.context.resources.getDimensionPixelSize(R.dimen.size_1)
                )
            )
            this.submitList(memoList)
        }
    }
}

@BindingAdapter("memoTagColor")
fun bindingTagColor(
    view: View,
    tag: Int
) {
    when (tag) {
        TagType.RED.tag -> {
            view.setBackgroundResource(TagType.RED.color)
        }
        TagType.ORANGE.tag -> {
            view.setBackgroundResource(TagType.ORANGE.color)
        }
        TagType.YELLOW.tag -> {
            view.setBackgroundResource(TagType.YELLOW.color)
        }
        TagType.GREEN.tag -> {
            view.setBackgroundResource(TagType.GREEN.color)
        }
        TagType.BLUE.tag -> {
            view.setBackgroundResource(TagType.BLUE.color)
        }
        TagType.PURPLE.tag -> {
            view.setBackgroundResource(TagType.PURPLE.color)
        }
        else -> {
            view.setBackgroundResource(TagType.ETC.color)
        }
    }
}

