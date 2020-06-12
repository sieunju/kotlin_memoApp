package com.hmju.memo.ui.bindingadapter

import android.os.Build
import android.text.Html
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.hmju.memo.utils.JLogger

/**
 * Description : 공통 Binding Adapter
 *
 * Created by hmju on 2020-06-12
 */

@BindingAdapter("commonText")
fun bindingText(
    textView: AppCompatTextView,
    text: String?
){
    text?.let{
        textView.text = text
    } ?: run{
        JLogger.d("왜 널이요???")
    }
}

@BindingAdapter("commonHtmlText")
fun bindingHtmlText(
    textView: AppCompatTextView,
    text: String?
){
    text?.let{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            textView.text = Html.fromHtml(it)
        } else {
            textView.text = Html.fromHtml(it,Html.FROM_HTML_MODE_LEGACY)
        }
    } ?: run{
        JLogger.d("왜 널이요?")
    }
}
