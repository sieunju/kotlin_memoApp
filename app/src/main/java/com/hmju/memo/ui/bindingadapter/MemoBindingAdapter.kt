package com.hmju.memo.ui.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoImagePagerAdapter
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.ui.decoration.LinearItemDecoration
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel

/**
 * Description : 메모 리스트 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter(value = ["viewModel", "memoData"])
fun setMemoListAdapter(
    view: RecyclerView,
    viewModel: MainViewModel,
    memoList: PagedList<MemoItem>?
) {
    view.adapter?.let { adapter ->

        if (adapter is MemoListAdapter) {
            JLogger.d("TEST:: 갱신 갱신")
            adapter.submitList(memoList)
        }
    } ?: run {
        MemoListAdapter(viewModel).apply {
            view.adapter = this
            view.addItemDecoration(
                LinearItemDecoration(
                    divider = view.context.resources.getDimensionPixelSize(R.dimen.size_1)
                )
            )
            this.submitList(memoList)
            JLogger.d("TEST:: 추가")
        }
    }
}

@BindingAdapter("memoTagColor")
fun bindingTagColor(
    view: View,
    tag: Int
) {
    when (tag) {
        1 -> {
            view.setBackgroundResource(R.color.color_tag1)
        }
        2 -> {
            view.setBackgroundResource(R.color.color_tag2)
        }
        3 -> {
            view.setBackgroundResource(R.color.color_tag3)
        }
        4 -> {
            view.setBackgroundResource(R.color.color_tag4)
        }
        5 -> {
            view.setBackgroundResource(R.color.color_tag5)
        }
        6 -> {
            view.setBackgroundResource(R.color.color_tag6)
        }
        else -> {
            view.setBackgroundResource(R.color.color_tag7)
        }

    }
}

@BindingAdapter("imgList")
fun bindingMemoDetailViewPager(
    viewPager : ViewPager2,
    dataList : ArrayList<String>?
) {
    dataList?.let {
        viewPager.visibility = View.VISIBLE

        viewPager.adapter?.let{
            it.notifyDataSetChanged()
        } ?: run {
            val adapter = MemoImagePagerAdapter(dataList)
            viewPager.adapter = adapter
        }
    } ?: {
        viewPager.visibility = View.GONE
    } ()
}