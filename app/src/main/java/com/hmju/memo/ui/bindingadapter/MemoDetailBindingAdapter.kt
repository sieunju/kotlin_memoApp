package com.hmju.memo.ui.bindingadapter

import android.content.DialogInterface
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.define.TagType
import com.hmju.memo.dialog.CommonDialog
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.ui.adapter.MemoDetailMoreAdapter
import com.hmju.memo.ui.adapter.MemoImagePagerAdapter
import com.hmju.memo.ui.bottomsheet.MemoMoreDialog
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MemoAddViewModel
import com.hmju.memo.viewModels.MemoDetailViewModel
import com.hmju.memo.viewModels.MemoEditViewModel
import com.hmju.memo.widget.viewpagerIndicator.IndicatorView

/**
 * Description : 메모장 자세히 보기 Binding Adapter
 *
 * Created by juhongmin on 2020/09/16
 */

@BindingAdapter(value = ["viewModel", "indicatorView", "fileList"], requireAll = false)
fun setMemoDetailImgAdapter(
    viewPager: ViewPager2,
    viewModel: BaseViewModel,
    indicator: IndicatorView,
    dataList: ArrayList<FileItem>?
) {
    dataList?.let {
        viewPager.visibility = View.VISIBLE
        indicator.visibility = View.VISIBLE

        viewPager.adapter?.let {
            JLogger.d("갱신 처리 합니다.!! ${dataList.size}")
            it.notifyDataSetChanged()
            indicator.pageSize = dataList.size
        } ?: run {

            // init View..
            MemoImagePagerAdapter(viewModel, dataList).apply {
                viewPager.adapter = this
            }

            indicator.apply {
                setViewPager(viewPager)
                pageSize = dataList.size
            }
        }
    } ?: {
        JLogger.d("여길 지나나!!!!??")
        viewPager.visibility = View.GONE
        indicator.visibility = View.GONE
    }()
}

@BindingAdapter(value = ["memoDetailMoreDataList", "listener"])
fun setMemoDetailMoreAdapter(
    recyclerView: RecyclerView,
    dataList: ArrayList<MemoMoreDialog.MemoDetailMoreDialogItem>,
    listener: MemoMoreDialog.Listener
) {
    recyclerView.adapter?.notifyDataSetChanged() ?: run {
        MemoDetailMoreAdapter(dataList, listener).apply {
            recyclerView.adapter = this
        }
    }
}

@BindingAdapter("viewModel")
fun setMemoDetailTagAdapter(
    radioGroup: RadioGroup,
    viewModel: BaseViewModel
) {
    radioGroup.setOnCheckedChangeListener { _, checkedId ->
        val tagType: TagType
        when (checkedId) {
            R.id.tag_1 -> {
                tagType = TagType.RED
            }
            R.id.tag_2 -> {
                tagType = TagType.ORANGE
            }
            R.id.tag_3 -> {
                tagType = TagType.YELLOW
            }
            R.id.tag_4 -> {
                tagType = TagType.GREEN
            }
            R.id.tag_5 -> {
                tagType = TagType.BLUE
            }
            R.id.tag_6 -> {
                tagType = TagType.PURPLE
            }
            else -> {
                tagType = TagType.ETC
            }
        }

        if (viewModel is MemoDetailViewModel) {
            viewModel.setSelectedTag(tagType)
        } else if (viewModel is MemoAddViewModel) {
            viewModel.setSelectedTag(tagType)
        }
    }

    if (viewModel is MemoDetailViewModel) {
        // Binding RadioButton
        when (viewModel.selectTag.value) {
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
    } else if (viewModel is MemoAddViewModel) {
        // Binding RadioButton
        when (viewModel.selectTag.value) {
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
    }
}

@BindingAdapter(value = ["viewModel", "fileItem"], requireAll = false)
fun setMemoDetailImgLongClickListener(
    view: AppCompatImageView,
    viewModel: BaseViewModel,
    item: FileItem
) {
    view.setOnLongClickListener {
        JLogger.d("onLongClick!!")
        CommonDialog(view.context)
            .setContents(R.string.str_memo_img_delete)
            .setPositiveButton(R.string.str_confirm)
            .setNegativeButton(R.string.str_cancel)
            .setListener(object : CommonDialog.Listener {
                override fun onClick(which: Int) {
                    if(which == CommonDialog.POSITIVE) {
                        if (viewModel is MemoEditViewModel) {
                            viewModel.deleteImage(item)
                        }
                    }
                }
            })
            .show()
        return@setOnLongClickListener true
    }
}

//@InverseBindingAdapter(attribute = "selectTag", event = "radioTagChanged")
//fun getSelectedTagRadio(view: RadioGroup): Int {
//    JLogger.d("getSelectedTagRadio\t${view.tag}")
//    view.tag?.let {
//        return it.toString().toInt()
//    } ?: run {
//        return -1
//    }
//}

//@BindingAdapter("radioTagChanged")
//fun setRadioTagChangedListener(
//    view: RadioGroup,
//    radioChanged: InverseBindingListener
//) {
//    view.setOnCheckedChangeListener { _, checkedId ->
//        when (checkedId) {
//            R.id.tag_1 -> {
//                view.tag = TagType.RED.color
//            }
//            R.id.tag_2 -> {
//                view.tag = TagType.ORANGE.color
//            }
//            R.id.tag_3 -> {
//                view.tag = TagType.YELLOW.color
//            }
//            R.id.tag_4 -> {
//                view.tag = TagType.GREEN.color
//            }
//            R.id.tag_5 -> {
//                view.tag = TagType.BLUE.color
//            }
//            R.id.tag_6 -> {
//                view.tag = TagType.PURPLE.color
//            }
//            else -> {
//                view.tag = TagType.ETC.color
//            }
//        }
//        radioChanged.onChange()
//    }
//}


