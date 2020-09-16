package com.hmju.memo.ui.bindingadapter

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.define.TagType
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoImagePagerAdapter
import com.hmju.memo.utils.JLogger

/**
 * Description : 메모장 자세히 보기 Binding Adapter
 *
 * Created by juhongmin on 2020/09/16
 */

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

/**
 * 메모 상세 테그 선택 관련 Binding 처리.
 */
@BindingAdapter(value = ["bgView", "selectTag"], requireAll = false)
fun bindingSelectTag(
    radioGroup: RadioGroup,
    selectedView: View,
    item: NonNullMutableLiveData<MemoItem>
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

        selectedView.setBackgroundColor(ContextCompat.getColor(selectedView.context, tagType.color))
        ObjectAnimator.ofFloat(selectedView,View.ALPHA,0.25F,1.0F).apply {
            duration = 500
            start()
        }
        item.value.tag = tagType.tag
    }

    // Binding View
    when (item.value.tag) {
        TagType.RED.tag -> {
            radioGroup.check(R.id.tag_1)
        }
        TagType.ORANGE.tag -> {
            radioGroup.check(R.id.tag_2)

        }
        TagType.YELLOW.tag -> {
            radioGroup.tag = TagType.YELLOW.tag
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

