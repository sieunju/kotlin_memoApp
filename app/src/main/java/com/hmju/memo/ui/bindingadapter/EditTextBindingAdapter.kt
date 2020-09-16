package com.hmju.memo.ui.bindingadapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.text.Html
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MemoDetailViewModel

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/07
 */
@Suppress("DEPRECATION")
@BindingAdapter("htmlText")
fun setHtmlTextInputEditText(
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

@InverseBindingAdapter(attribute = "htmlText", event = "android:textAttrChanged")
fun getHtmlText(editText: TextInputEditText): String {
    return editText.text.toString()
}

@Suppress("DEPRECATION")
@BindingAdapter("htmlText")
fun setHtmlTextLiveData(
    textView: TextInputEditText,
    data: LiveData<String>
) {
    data.value?.let{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(it),TextView.BufferType.EDITABLE)
        } else {
            textView.setText(
                Html.fromHtml(it,Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.EDITABLE
            )
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("editTextListener")
fun setEditTextListener(
    editText: TextInputEditText,
    viewModel: BaseViewModel
) {
    // 긴 문장 입력시 스크롤 되도록 처리.
    editText.setOnTouchListener { view, event ->
        if (view.isFocused) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    // 입력란 안에 있는 경우 입력란 터치 우선 순위.
                    if (outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
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

    // 복사 리스너 세팅
    editText.setOnLongClickListener { v ->
        if (viewModel is MemoDetailViewModel) {
            viewModel.onCopyText(v.id)
        }
        return@setOnLongClickListener true
    }
}




