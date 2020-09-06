package com.hmju.memo.base

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startAct

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
}