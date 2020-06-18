package com.hmju.memo.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.model.memo.MemoImgItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoNormaItem
import com.hmju.memo.utils.JLogger

/**
 * Description: BaseViewHolder
 *
 * Created by juhongmin on 2020/06/05
 */
abstract class BaseViewHolder<out T : ViewDataBinding>(
    parent: ViewGroup,
    @LayoutRes private val layoutId: Int
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
) {

    val binding: T = DataBindingUtil.bind(itemView)!!

    val memoClick = object : BaseClickListener {

        override fun memoClick(item: MemoItem) {
            JLogger.d("메모 클릭!했습니다." + item.manageNo)
        }

        override fun memoClick(item: MemoNormaItem) {
            JLogger.d("기본 타입 클릭!했습니다." + item.id)
        }

        override fun memoClick(item: MemoImgItem) {
            JLogger.d("이미지 타입 클릭!했습니다." + item.id)
        }
    }

    interface BaseClickListener {
        fun memoClick(item: MemoItem)
        fun memoClick(item: MemoNormaItem)
        fun memoClick(item: MemoImgItem)
    }
}