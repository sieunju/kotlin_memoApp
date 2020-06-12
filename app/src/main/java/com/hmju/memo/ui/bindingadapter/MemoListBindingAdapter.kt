package com.hmju.memo.ui.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel

/**
 * Description : 메모 리스트 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter(value=["viewModel","memoItems"])
fun setMemoListAdapter(
    view: RecyclerView,
    viewModel: MainViewModel,
    dataList: ListMutableLiveData<MemoItem>
) {
    view.adapter?.run{
        if(this is MemoListAdapter){
            JLogger.d("TEST:: 갱신 갱신")
            notifyDataSetChanged()
        }
    } ?: run{
        MemoListAdapter(viewModel,dataList).apply {
            view.adapter = this
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