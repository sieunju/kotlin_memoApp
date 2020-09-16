package com.hmju.memo.ui.bindingadapter

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel
import com.hmju.memo.viewModels.MemoDetailViewModel
import com.hmju.memo.widget.bottomToolbar.BottomToolbar

/**
 * Description : 공통 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter("text")
fun bindingText(
    textView: AppCompatTextView,
    text: String?
) {
    text?.let {
        textView.text = text
    }
}

@Suppress("DEPRECATION")
@BindingAdapter("htmlText")
fun bindingHtmlText(
    textView: AppCompatTextView,
    text: String?
) {
    text?.let {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(it)
        } else {
            textView.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
        }
    }
}

class OnSingleClickListener(private val onSingleCLick: (View) -> Unit) : View.OnClickListener {
    companion object {
        const val CLICK_INTERVAL = 500
    }

    private var lastClickedTime: Long = 0L

    override fun onClick(v: View?) {
        v?.let {
            if (isSafe()) {
                onSingleCLick(it)
            }
            lastClickedTime = System.currentTimeMillis()
        }
    }

    private fun isSafe() = System.currentTimeMillis() - lastClickedTime > CLICK_INTERVAL
}

fun View.setOnSingleClickListener(onSingleCLick: (View) -> Unit) {
    val singleClickListener = OnSingleClickListener {
        onSingleCLick(it)
    }
    setOnClickListener(singleClickListener)
}


/**
 * 뷰 중복 클릭 방지 리스너.
 */
@BindingAdapter("turtleClick")
fun setTurtleClick(
    view: View,
    listener: View.OnClickListener
) {
    view.setOnClickListener(OnSingleClickListener {
        listener.onClick(it)
    })

//    view.setOnSingleClickListener { listener.onClick(view) }

}

@BindingAdapter("viewModel")
fun setBottomToolBarClick(
    toolbar: BottomToolbar,
    viewModel: BaseViewModel
) {
    if (viewModel is MainViewModel) {
        toolbar.setOnItemReselectedListener { pos ->
            JLogger.d("ToolBar Click!!! $pos")
            viewModel.startToolBarAction.value = pos
        }
        toolbar.setOnItemSelectedListener { pos ->
            viewModel.startToolBarAction.value = pos
        }
    }
}

@BindingAdapter(value = ["dayResId", "nightResId"])
fun bindingImageViewDayNight(
    imgView: AppCompatImageView,
    dayResId: Drawable,
    nightResId: Drawable
) {
    val nightMode =
        imgView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    if (nightMode == Configuration.UI_MODE_NIGHT_NO) {
        JLogger.d("모드가 변경되었습니다. 라이트 모드")
        imgView.setImageDrawable(dayResId)
    } else {
        JLogger.d("모드가 변경되었습니다. 다크 모드")
        imgView.setImageDrawable(nightResId)
    }
}