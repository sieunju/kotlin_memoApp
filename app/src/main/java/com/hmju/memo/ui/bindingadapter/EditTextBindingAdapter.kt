package com.hmju.memo.ui.bindingadapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.viewmodels.LoginViewModel
import com.hmju.memo.viewmodels.MainViewModel
import com.hmju.memo.viewmodels.MemoDetailViewModel

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/07
 */
@Suppress("DEPRECATION")
@BindingAdapter("htmlEdit")
fun setHtmlEdit(
    view: TextView,
    text: String?
) {
    text?.let { newText ->
        val oldText = view.text.toString()

        // 변경점이 없는 경우. 리턴
        if (newText == oldText) {
            return
        }

        // 버전 별로 분기 처리.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            view.setText(Html.fromHtml(newText), TextView.BufferType.EDITABLE)
        } else {
            view.setText(
                Html.fromHtml(newText, Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.EDITABLE
            )
        }
    }
}

@InverseBindingAdapter(attribute = "htmlEdit", event = "android:textAttrChanged")
fun getHtmlTextString(view: TextView): String {
    return view.text.toString()
}

//@BindingAdapter("android:textAttrChanged", requireAll = false)
//fun setEditTextChanged(
//    view: TextView,
//    textChanged: InverseBindingListener
//) {
//    val listener = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//        }
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            textChanged.onChange()
//        }
//
//        override fun afterTextChanged(s: Editable?) {
//        }
//    }
//    view.removeTextChangedListener(listener)
//    view.addTextChangedListener(listener)
//}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("editTextListener")
fun setEditTextListener(
    editText: TextInputEditText,
    viewModel: BaseViewModel
) {

    when (viewModel) {
        is LoginViewModel -> {
            // Ket Action Listener
            editText.setOnEditorActionListener { v, actionId, event ->
                if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.startLogin()
                }
                return@setOnEditorActionListener false
            }
        }
        is MainViewModel -> {

        }
        is MemoDetailViewModel -> {
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
                viewModel.onCopyText(v.id)
                return@setOnLongClickListener true
            }
        }
    }
}




