package com.hmju.memo.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hmju.memo.model.memo.MemoImgItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoNormaItem
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.ui.widgt.updateAppWidget
import com.hmju.memo.ui.widgt.updateWidget
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startAct
import com.hmju.memo.utils.startActResult

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
            ((itemView.context) as Activity).startAct<LoginActivity> {}
        }

        override fun memoClick(item: MemoNormaItem) {
            JLogger.d("기본 타입 클릭!했습니다." + item.id)
        }

        override fun memoClick(item: MemoImgItem) {
            JLogger.d("이미지 타입 클릭!했습니다." + item.id)
            Glide.with(itemView.context)
                .asBitmap()
                .load("https://t1.daumcdn.net/webtoon/op/ec8b80648ad6662d692ddce3962a13c3b918ad46")
                .into(object : CustomTarget<Bitmap>() {

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        updateWidget(
                            context = itemView.context,
                            logo = "올리브영",
                            logoImg = resource,
                            title = item.title,
                            contents = item.contents
                        )
                    }
                })

        }
    }

    interface BaseClickListener {
        fun memoClick(item: MemoItem)
        fun memoClick(item: MemoNormaItem)
        fun memoClick(item: MemoImgItem)
    }
}