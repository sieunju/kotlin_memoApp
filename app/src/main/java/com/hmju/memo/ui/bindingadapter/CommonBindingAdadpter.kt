package com.hmju.memo.ui.bindingadapter

import android.os.Build
import android.text.Html
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel
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

@BindingAdapter("commonHtmlText")
fun bindingHtmlMaterialText(
    textView : MaterialTextView,
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
            if(isSafe()){
                onSingleCLick(it)
            }
            lastClickedTime = System.currentTimeMillis()
        }
    }

    private fun isSafe() = System.currentTimeMillis() - lastClickedTime > CLICK_INTERVAL
}

fun View.setOnSingleClickListener(onSingleCLick: (View) -> Unit){
    val singleClickListener = OnSingleClickListener{
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
   if(viewModel is MainViewModel) {
       toolbar.setOnItemReselectedListener { pos->
           JLogger.d("ToolBar Click!!! $pos")
           viewModel.startToolBarAction.value = pos
       }
       toolbar.setOnItemSelectedListener { pos->
           viewModel.startToolBarAction.value = pos
       }
   }
}