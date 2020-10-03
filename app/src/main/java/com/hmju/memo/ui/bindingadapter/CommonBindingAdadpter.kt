package com.hmju.memo.ui.bindingadapter

import android.graphics.PorterDuff
import android.os.Build
import android.text.Html
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.ui.adapter.BottomSheetCheckableAdapter
import com.hmju.memo.ui.bottomsheet.CheckableBottomSheet
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel
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

@BindingAdapter("filterColorId")
fun setImageColorFilter(
    imgView: AppCompatImageView,
    @ColorInt colorResId: Int
) {
    imgView.setColorFilter(colorResId, PorterDuff.Mode.SRC_IN)
}

@BindingAdapter(value = ["checkableDialogDataList", "listener"])
fun setSelectBottomSheetAdapter(
    recyclerView: RecyclerView,
    dataList: List<CheckableBottomSheet.CheckableBottomSheetItem>,
    listener: CheckableBottomSheet.Listener
) {
    recyclerView.adapter?.notifyDataSetChanged() ?: run {
        BottomSheetCheckableAdapter(dataList, listener).apply {
            recyclerView.adapter = this
        }
    }
}

@BindingAdapter("visibility")
fun setVisibility(
    view: View,
    visible: Boolean
) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("recyclerView")
fun setFloatingButtonListener(
    view: FloatingActionButton,
    recyclerView: RecyclerView
) {
    view.hide()
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                view.show()
            } else {
                view.hide()
            }
        }
    })

    view.setOnClickListener {
        recyclerView.scrollToPosition(0)
        view.hide()
    }
}

@BindingAdapter("visibilityDataList")
fun setDataListVisibility(
    view: View,
    dataList: ArrayList<*>?
) {
    dataList?.let {
        if (it.size > 0) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } ?: {
        view.visibility = View.GONE
    }()
}

@BindingAdapter("drawableId")
fun bindingResourceDrawable(
    view: AppCompatImageView,
    @DrawableRes drawableId : Int
) {
    view.setImageResource(drawableId)
}
