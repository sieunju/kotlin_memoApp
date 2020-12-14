package com.hmju.memo.ui.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.model.test.TestBaseImgModel
import com.hmju.memo.model.test.TestNormalBannerImg
import com.hmju.memo.model.test.TestUiModel
import com.hmju.memo.ui.adapter.TestItemAdapter
import com.hmju.memo.ui.adapter.TestListAdapter
import com.hmju.memo.ui.decoration.HorizontalItemDecoration
import com.hmju.memo.ui.decoration.VerticalItemDecoration
import com.hmju.memo.utils.JLogger


@BindingAdapter(value = ["testDataList"])
fun setTestDataListAdapter(
    view: RecyclerView,
    dataList: ArrayList<TestUiModel>
) {
    view.adapter?.also { adapter ->
        if (adapter is TestListAdapter) {
            adapter.dataList = dataList
            adapter.notifyDataSetChanged()
        }
    } ?: run {
        TestListAdapter().apply {
            view.adapter = this
            this.dataList = dataList
            view.addItemDecoration(
                VerticalItemDecoration(divider = view.resources.getDimensionPixelOffset(R.dimen.size_10))
            )
        }
    }
}

@BindingAdapter(value = ["testImageList"])
fun setTestImageItemListAdapter(
    view: RecyclerView,
    dataList: ArrayList<out TestBaseImgModel>
) {
    view.adapter?.also { adapter ->
        if (adapter is TestItemAdapter) {
            adapter.dataList = dataList as ArrayList<TestBaseImgModel>
            adapter.notifyDataSetChanged()
            JLogger.d("데이터 사이즈  ${dataList.size}")
        }
    } ?: run {
        TestItemAdapter().apply {
            JLogger.d("데이터 사이즈!우 아래 ${dataList.size}")
            view.adapter = this
            this.dataList = dataList as ArrayList<TestBaseImgModel>
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            view.addItemDecoration(
                HorizontalItemDecoration(
                    side = view.resources.getDimensionPixelSize(R.dimen.size_20),
                    divider = view.resources.getDimensionPixelOffset(R.dimen.size_2)
                )
            )
        }
    }
}