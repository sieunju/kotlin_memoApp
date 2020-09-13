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

@BindingAdapter(value = ["viewModel", "memoData"])
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

@BindingAdapter("fileList")
fun bindingMemoDetailViewPager(
    viewPager: ViewPager2,
    dataList: ArrayList<FileItem>?
) {
    dataList?.let {
        viewPager.visibility = View.VISIBLE
        viewPager.adapter?.let {
            it.notifyDataSetChanged()
        } ?: run {
            val adapter = MemoImagePagerAdapter(dataList)
            viewPager.adapter = adapter
        }
    } ?: {
        viewPager.visibility = View.GONE
    }()
}

@BindingAdapter("selectTag")
fun bindingSelectTag(
    radioGroup: RadioGroup,
    data: MutableLiveData<MemoItem>
) {
    // 데이터 세팅.
    when (data.value?.tag) {
        TagType.RED.tag -> {
            radioGroup.check(R.id.tag_1)
        }
        TagType.ORANGE.tag -> {
            radioGroup.check(R.id.tag_2)
        }
        TagType.YELLOW.tag -> {
            radioGroup.check(R.id.tag_3)
        }
        TagType.GREEN.tag -> {
            radioGroup.check(R.id.tag_4)
        }
        TagType.BLUE.tag -> {
            radioGroup.check(R.id.tag_5)
        }
        TagType.PURPLE.tag -> {
            radioGroup.check(R.id.tag_6)
        }
        else -> {
            radioGroup.check(R.id.tag_7)
        }
    }

    radioGroup.setOnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.tag_1 -> {
                data.value?.tag = TagType.RED.tag
            }
            R.id.tag_2 -> {
                data.value?.tag = TagType.ORANGE.tag
            }
            R.id.tag_3 -> {
                data.value?.tag = TagType.YELLOW.tag
            }
            R.id.tag_4 -> {
                data.value?.tag = TagType.GREEN.tag
            }
            R.id.tag_5 -> {
                data.value?.tag = TagType.BLUE.tag
            }
            R.id.tag_6 -> {
                data.value?.tag = TagType.PURPLE.tag
            }
            else -> {
                data.value?.tag = TagType.ETC.tag
            }
        }
    }
}