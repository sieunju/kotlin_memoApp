package com.hmju.memo.widget.pagertablayout

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import androidx.annotation.Dimension
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.R
import com.hmju.memo.databinding.ItemPagerTabLayoutBinding
import com.hmju.memo.databinding.ViewPagerTabStripBinding
import com.hmju.memo.utils.JLogger

/**
 * Description : ViewPager2 기반의 PagerTabStrip
 *
 * Created by hmju on 2020-12-29
 */
class PagerTabLayout(private val ctx: Context, attrs: AttributeSet?) :
        HorizontalScrollView(ctx, attrs), LifecycleOwner, LifecycleObserver {

    interface Listener {
        fun onTabClick(pos: Int)
        fun onPageScrolled(pos: Int, offset: Float)
        fun onPageSelected(pos: Int)
        fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int)
    }

    open class SimpleListener : Listener {
        override fun onTabClick(pos: Int) {}
        override fun onPageScrolled(pos: Int, offset: Float) {}
        override fun onPageSelected(pos: Int) {}
        override fun onPageScrollStateChanged(state: Int) {}
    }

    private val tabContainer: ViewPagerTabStripBinding by lazy {
        DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.view_pager_tab_strip, this, false)
    }
    private val lifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    enum class Type {
        FIXED, SCROLLABLE
    }

    /**
     * PagerTabLayout Listener
     * example..
     * Listener 를 가로 채고 싶다면. Data Setting 하기전에 setListener 해야 한다.
     * PagerTabLayout.listener = object : PagerTabLayout.SimpleListener () {
     *      override fun onTabClick(pos: Int) {}
     * }
     */
    var listener: Listener? = object : SimpleListener() {
        override fun onTabClick(pos: Int) {
            if (currentPos != pos) {
                currentPos = pos
                viewPager?.setCurrentItem(pos, false)
                invalidate()

                updateTab(pos)
            }
        }
    }

    /**
     * ViewPager2 PageListener
     */
    private val pageListener = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(pos: Int) {
            listener?.onPageSelected(pos)
        }

        override fun onPageScrolled(pos: Int, offset: Float, offsetPixel: Int) {
            // Offset 튀는 현상 방지 코드.
            if (offset < 1F) {
                currentPos = pos
                posScrollOffset = offset

                scrollToChild(pos, (offset * (tabContainer.root as LinearLayoutCompat).getChildAt(pos).width).toInt())

                // ReDraw
                invalidate()
            }

            listener?.onPageScrolled(pos, offset)
        }

        override fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int) {
            if (ViewPager2.SCROLL_STATE_IDLE == state) {
                updateTab(currentPos)
            }

            listener?.onPageScrollStateChanged(state)
        }
    }

    var viewPager: ViewPager2? = null
        set(value) {
            value?.unregisterOnPageChangeCallback(pageListener)
            value?.registerOnPageChangeCallback(pageListener)

            field = value
        }

    // setDataList
    var dataList = arrayListOf<PagerTabLayoutDataModel>()
        set(value) {
            tabCount = value.size
            if (tabContainer.root is LinearLayoutCompat) {
                (tabContainer.root as LinearLayoutCompat).removeAllViews()

                val layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT)

                if (type == Type.FIXED) {
                    (tabContainer.root as LinearLayoutCompat).weightSum = tabCount.toFloat()
                    layoutParams.weight = 1F
                }

                val layoutInflater = LayoutInflater.from(ctx)
                value.forEachIndexed { index, data ->
                    data.pos = index
                    data.isSelected.value = index == currentPos
                    textColorStyle?.let { data.textColorStyle = it }
                    iconWidth?.let { data.iconWidth = it }

                    val binding = ItemPagerTabLayoutBinding.inflate(layoutInflater, this, false)
                    binding.listener = listener
                    binding.item = value[index]
                    binding.lifecycleOwner = this

                    (binding.root as LinearLayoutCompat).layoutParams = layoutParams
                    (tabContainer.root as LinearLayoutCompat).addView(binding.root)
                }

                invalidate()
            }

            field = value
        }

    // [s] Attribute Set Variable
    private var type: Type = Type.FIXED
    private var textColorStyle: ColorStateList? = null

    @Dimension
    private var indicatorHeight: Float = -1F
    private var scrollingOffset = 0

    @Dimension
    private var iconWidth: Int? = null
    // [e] Attribute Set Variable

    private val indicatorPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    private var posScrollOffset: Float = -1F // Scroll Offset.
    private var lastScrollX = 0

    private var tabCount: Int = 0 // Tab Count.
    private var currentPos: Int = 0 // 현재 위치값.

    init {
        if (isInEditMode) throw IllegalStateException("isInEditMode Error..!")

        isFillViewport = true
        setWillNotDraw(false)
        isHorizontalScrollBarEnabled = false

        // 속성값 세팅.
        attrs?.also {
            val attr: TypedArray = ctx.obtainStyledAttributes(it, R.styleable.PagerTabLayout)

            type = Type.values()[attr.getInt(R.styleable.PagerTabLayout_pagerTab_type, 0)]
            textColorStyle = attr.getColorStateList(R.styleable.PagerTabLayout_pagerTab_textStyle)
            attr.getColor(R.styleable.PagerTabLayout_pagerTab_indicatorColor, -1).let { color ->
                if (color != -1) {
                    indicatorPaint.color = color
                }
            }
            indicatorHeight = attr.getDimension(R.styleable.PagerTabLayout_pagerTab_indicatorHeight, -1F)
            scrollingOffset = attr.getDimensionPixelOffset(R.styleable.PagerTabLayout_pagerTab_scrollingOffset, 0)

            if (attr.hasValue(R.styleable.PagerTabLayout_pagerTab_icon_width)) {
                iconWidth = attr.getDimensionPixelSize(R.styleable.PagerTabLayout_pagerTab_icon_width, -1)
            }

            attr.recycle()
        }

        tabContainer.lifecycleOwner = this

        addView(tabContainer.root)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStateEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    override fun getLifecycle() = lifecycleRegistry

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode || tabCount == 0) return

        // Draw Indicator Line
        if (indicatorHeight == -1F) return

        with(tabContainer.root as LinearLayoutCompat) {
            val currentTab: View? = if (this.childCount > currentPos) getChildAt(currentPos) else null
            var lineLeft: Float = currentTab?.left?.toFloat() ?: 0F
            var lineRight: Float = currentTab?.right?.toFloat() ?: 0F

            // Scroll 하는 경우 Indicator 자연스럽게 넘어가기 위한 로직.
            if (posScrollOffset > 0F && currentPos < tabCount - 1) {
                val nextTab = getChildAt(currentPos + 1)
                lineLeft = (posScrollOffset * nextTab.left + (1F - posScrollOffset) * lineLeft)
                lineRight = (posScrollOffset * nextTab.right + (1F - posScrollOffset) * lineRight)
            }

            canvas?.drawRect(lineLeft, height - indicatorHeight, lineRight, height.toFloat(), indicatorPaint)
        }
    }

    /**
     * Update Tab Style.
     */
    fun updateTab(pos: Int) {
        dataList.forEach { data ->
            data.isSelected.value = data.pos == pos
        }
    }

    /**
     * 한 화면에 보여지는 Tab 이 Over 되는 경우
     * Scrolling 처리 함수.
     * @param pos Current Pos
     * @param offset Scrolling Offset
     */
    private fun scrollToChild(pos: Int, offset: Int) {
        if (tabCount == 0) return

        var newScrollX = (tabContainer.root as LinearLayoutCompat).getChildAt(pos).left + offset

        if (pos > 0 || offset > 0) {
            newScrollX -= scrollingOffset
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            scrollTo(newScrollX, 0)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentPos = savedState.currentPos
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        JLogger.d("onSaveInstanceState ")
        super.onSaveInstanceState()?.let {
            val state = SavedState(it)
            state.currentPos = currentPos
            return state
        } ?: run {
            return null
        }
    }

    class SavedState : BaseSavedState {
        var currentPos: Int = 0

        constructor(state: Parcelable) : super(state) {}

        constructor(state: Parcel) : super(state) {
            currentPos = state.readInt()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(currentPos)
        }

        companion object {
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}