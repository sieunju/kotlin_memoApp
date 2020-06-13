package com.hmju.memo.base

import android.app.Activity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startAct
import com.hmju.memo.utils.startActResult

/**
 * Description: BaseViewHolder
 *
 * Created by juhongmin on 2020/06/05
 */
abstract class BaseViewHolder<out T : ViewDataBinding>(private val view: View) : RecyclerView.ViewHolder(view) {
    val binding: T = DataBindingUtil.bind(view)!!

    val memoClick = object : BaseClickListener {

        override fun memoClick(item: MemoItem) {
            JLogger.d("메모 클릭!했습니다." + item.manageNo)
            ((view.context) as Activity).startAct<LoginActivity>{}
        }
    }

    interface BaseClickListener {
        fun memoClick(item: MemoItem)
    }
}