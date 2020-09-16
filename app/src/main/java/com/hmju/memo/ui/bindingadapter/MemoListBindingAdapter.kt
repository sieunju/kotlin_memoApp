package com.hmju.memo.ui.bindingadapter

import android.view.View
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.define.TagType
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoImagePagerAdapter
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.ui.decoration.LinearItemDecoration
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
            adapter.submitList(memoList)
        }
    } ?: run {
        MemoListAdapter(viewModel).apply {
            view.adapter = this
            view.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            view.addItemDecoration(
                LinearItemDecoration(
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

