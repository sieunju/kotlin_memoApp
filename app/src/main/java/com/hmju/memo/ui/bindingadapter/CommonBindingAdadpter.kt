package com.hmju.memo.ui.bindingadapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel
import com.hmju.memo.viewModels.MemoDetailViewModel
import com.hmju.memo.widget.BottomToolbar

/**
 * Description : 공통 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter("commonText")
fun bindingText(
    textView: AppCompatTextView,
    text: String?
) {
    text?.let {
        textView.text = text
    } ?: run {
        JLogger.d("왜 널이요???")
    }
}

@Suppress("DEPRECATION")
@BindingAdapter("commonHtmlText")
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
    } ?: run {
        JLogger.d("왜 널이요?")
    }
}

@Suppress("DEPRECATION")
@BindingAdapter("commonHtmlText")
fun bindingHtmlMaterialText(
    textView: MaterialTextView,
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

@Suppress("DEPRECATION")
@BindingAdapter("commonHtmlText")
fun bindingHtmlEditText(
    textView: TextInputEditText,
    text: String?
) {
    text?.let {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

            textView.setText(Html.fromHtml(it), TextView.BufferType.EDITABLE)
        } else {
            textView.setText(
                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.EDITABLE
            )
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

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter(value = ["editTextScrollListener","isTitle"])
fun setEditTextScrollListener(
    editText: TextInputEditText,
    viewModel: BaseViewModel,
    isTitle : Boolean
) {
    // 긴 문장 입력시 스크롤 되도록 처리.
    editText.setOnTouchListener { view, event ->
        if(view.isFocused) {
            when(event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    // 입력란 안에 있는 경우 입력란 터치 우선 순위.
                    if(outRect.contains(event.rawX.toInt(),event.rawY.toInt())){
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // 입력란 밖에 있는 경우 터치 우선 순위 해제.
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return@setOnTouchListener false
    }
    editText.setOnLongClickListener { _->
        if(viewModel is MemoDetailViewModel) {
            viewModel.onCopyText(isTitle)
        }
        return@setOnLongClickListener true
    }
}