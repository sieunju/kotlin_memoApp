package com.hmju.memo.ui.bindingadapter

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hmju.memo.base.BaseFragmentPagerAdapter
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.define.FragmentType
import com.hmju.memo.define.ListItemType
import com.hmju.memo.ui.adapter.BottomSheetCheckableAdapter
import com.hmju.memo.ui.adapter.ItemListAdapter
import com.hmju.memo.ui.bottomsheet.CheckableBottomSheet
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewmodels.MainViewModel
import com.hmju.memo.widget.bottomToolbar.BottomToolbar
import com.hmju.memo.widget.pagertablayout.PagerTabLayout
import com.hmju.memo.widget.pagertablayout.PagerTabLayoutDataModel
import java.text.DecimalFormat

object CommonBindingAdapter {
    /**
     * set TextView
     * default Type
     */
    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(
            textView: AppCompatTextView,
            text: String?
    ) {
        text?.let {
            textView.text = it
        }
    }

    /**
     * set TextView
     * htmlText Type
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    @BindingAdapter("htmlText")
    fun setHtmlText(
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

    /**
     * set TextView
     * String Format Type
     */
    @JvmStatic
    @BindingAdapter(value = ["fmtText", "data"], requireAll = true)
    fun setFmtTextData(
            textView: AppCompatTextView,
            fmtText: String,
            data: String?
    ) {
        data?.let {
            textView.text = fmtText
            textView.visibility = View.VISIBLE
        } ?: run {
            textView.visibility = View.GONE
        }
    }

    /**
     * set TextView
     * Number Comma Type
     */
    @JvmStatic
    @BindingAdapter("commaText")
    fun setCommaText(
            textView: AppCompatTextView,
            number: Int
    ) {
        val formatter = DecimalFormat("###,###")
        textView.text = formatter.format(number)
    }

    /**
     * set selector TextView
     */
    @JvmStatic
    @BindingAdapter("textColorStyle")
    fun setTextColorList(
            textView: AppCompatTextView,
            colorStyle: ColorStateList?
    ) {
        colorStyle?.let { textView.setTextColor(it) }
    }

    /**
     * set Typeface TextView
     */
    @JvmStatic
    @BindingAdapter("android:textStyle")
    fun setTextViewTypeFace(
            textView: AppCompatTextView,
            style: String
    ) {
        when (style) {
            "bold" -> {
                textView.setTypeface(null, Typeface.BOLD)
            }
            "normal" -> {
                textView.setTypeface(null, Typeface.NORMAL)
            }
            "italic" -> {
                textView.setTypeface(null, Typeface.ITALIC)
            }
            else -> {
            }
        }
    }

    /**
     * set Filter ImageView
     */
    @JvmStatic
    @BindingAdapter("filterColorId")
    fun setImageColorFilter(
            imgView: AppCompatImageView,
            @ColorInt colorResId: Int
    ) {
        imgView.setColorFilter(colorResId, PorterDuff.Mode.SRC_IN)
    }

    /**
     * set Resource ImageView
     * @param resourceId Nullable Id
     */
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewDrawable(
            imgView: AppCompatImageView,
            @DrawableRes resourceId: Int?
    ) {
        resourceId?.let {
            imgView.setImageResource(it)
        }
    }

    // [s] 중복 클릭 방지 리스너
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

    @JvmStatic
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
// [e] 중복 클릭 방지 리스너

    /**
     * Long Click
     */
    @JvmStatic
    @BindingAdapter("longClick")
    fun setLongClick(
            view: View,
            listener: View.OnClickListener
    ) {
        view.setOnLongClickListener(View.OnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            listener.onClick(it)
            return@OnLongClickListener true
        })
    }

    /**
     * Common BottomToolbar Click Listener
     * BottomToolbar 에서 충분히 할수 있을것으로 보임.
     */
    @JvmStatic
    @BindingAdapter("viewModel")
    fun setBottomToolBarClick(
            toolbar: BottomToolbar,
            viewModel: BaseViewModel
    ) {
        if (viewModel is MainViewModel) {
            toolbar.setOnItemReselectedListener { pos ->
                viewModel.startToolBarAction.value = pos
            }
            toolbar.setOnItemSelectedListener { pos ->
                viewModel.startToolBarAction.value = pos
            }
        }
    }

    /**
     * set CheckableBottomSheet DataList
     * @param dataList Check, Id, Name
     * @param listener Click Listener
     */
    @JvmStatic
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

    /**
     * set Floating Button Visible Listener
     */
    @JvmStatic
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

    /**
     * set View Visible
     */
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(
            view: View,
            visible: Boolean
    ) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * set View Visible
     * ArrayList Type
     */
    @JvmStatic
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

    /**
     * set View Selected
     */
    @JvmStatic
    @BindingAdapter("isSelected")
    fun setSelected(
            view: View,
            isSelect: Boolean
    ) {
        view.isSelected = isSelect
    }

    /**
     * set Layout Width or Height
     */
    @JvmStatic
    @BindingAdapter(value = ["layout_width", "layout_height"], requireAll = false)
    fun setLayoutWidthAndHeight(
            view: View,
            @Dimension width: Int?,
            @Dimension height: Int?
    ) {
        val layoutParams = view.layoutParams
        width?.let {
            layoutParams.width = when (it) {
                -1 -> ViewGroup.LayoutParams.MATCH_PARENT
                -2 -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> it
            }
        }

        height?.let {
            layoutParams.height = when (it) {
                -1 -> ViewGroup.LayoutParams.MATCH_PARENT
                -2 -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> it
            }
        }

        view.layoutParams = layoutParams
    }

    /**
     * set Pager Tab Layout
     */
    @JvmStatic
    @BindingAdapter(value = ["viewPager", "menuList"], requireAll = true)
    fun setPagerTabDataList(
            view: PagerTabLayout,
            viewPager: ViewPager2,
            dataList: ArrayList<PagerTabLayoutDataModel>?
    ) {
        view.viewPager = viewPager
        dataList?.let { list ->
            view.dataList = list
        } ?: run {
            JLogger.d("DataList Null")
        }
    }

    /**
     * Common ViewPager2 Adapter
     * @param limitSize ViewPager 수 제한.
     * @param type Unique Fragment Type
     * @param dataList DataList
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter(value = ["limitSize", "itemType", "dataList"], requireAll = false)
    fun <T : Any> setViewPagerAdapter(
            viewPager: ViewPager2,
            limitSize: Int = 0,
            type: FragmentType,
            dataList: ArrayList<T>
    ) {

        if (limitSize > 0) {
            viewPager.offscreenPageLimit = limitSize
        }

        viewPager.adapter?.also { adapter ->
            // DataList Changed
            (adapter as BaseFragmentPagerAdapter<T>).dataList = dataList
        } ?: run {

            var adapter: BaseFragmentPagerAdapter<T>? = null
            // 각 타입에 맞게 분기 처리한다.
            when (type) {
                FragmentType.TEST -> {
                    // Disable Swipe
//                viewPager.isUserInputEnabled = false
                }
                FragmentType.MAIN -> {

                }
                else -> {
                }
            }

            adapter?.let { viewPager.adapter = it }
        }
    }

    /**
     * Common RecyclerView ItemList Adapter
     * 단순 ItemListAdapter 인 경우 사용한다.
     * @param itemType Unique Item Type
     * @param dataList DataList
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter(value = ["itemType", "dataList"], requireAll = true)
    fun <T : Any> setItemListAdapter(
            recyclerView: RecyclerView,
            itemType: ListItemType,
            dataList: ArrayList<T>
    ) {
        recyclerView.adapter?.also { adapter ->
            (adapter as ItemListAdapter<T>).dataList = dataList
            adapter.notifyDataSetChanged()
        } ?: run {
            ItemListAdapter<T>().apply {
                type = itemType
                recyclerView.adapter = this
                this.dataList = dataList
            }
        }
    }
}



