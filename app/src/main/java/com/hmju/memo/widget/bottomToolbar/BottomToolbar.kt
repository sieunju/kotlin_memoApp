package com.hmju.memo.widget.bottomToolbar

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.customview.widget.ViewDragHelper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import com.hmju.memo.R
import com.hmju.memo.define.ToolBarDefine
import com.hmju.memo.ui.memo.MainActivity
import com.hmju.memo.ui.memo.MemoDetailActivity
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Description : BottomToolbar ViewClass
 * Open Source Library 사용
 *
 * Created by juhongmin on 2020/08/30
 */
class BottomToolbar(
    val ctx: Context,
    private val attrs: AttributeSet
) : View(ctx, attrs) {

    data class BottomBarItem(
        var title: String,
        val icon: Drawable,
        var rect: RectF = RectF(),
        var alpha: Int
    )

    interface OnItemSelectedListener {
        fun onItemSelect(pos: Int): Boolean
    }

    interface OnItemReselectedListener {
        fun onItemReselect(pos: Int)
    }

    // Dynamic Variables
    private var itemWidth: Float = 0F
    private var currentIconTint = itemIconTintActive
    private var indicatorLocation = barSideMargins
    private val rect = RectF()

    private var items = listOf<BottomBarItem>()

    var lifeCycle: Lifecycle? = null
        set(value) {
            field = value
            field?.addObserver(ToolbarLifecycleObserver(ctx as AppCompatActivity))
        }

    // Attribute Defaults
    @ColorInt
    private var _barBackgroundColor = Color.WHITE

    @ColorInt
    private var _barIndicatorColor = Color.parseColor(DEFAULT_INDICATOR_COLOR)

    @Dimension
    private var _barIndicatorRadius = context.d2p(DEFAULT_CORNER_RADIUS)

    @Dimension
    private var _barSideMargins = context.d2p(DEFAULT_SIDE_MARGIN)

    @Dimension
    private var _barCornerRadius = context.d2p(DEFAULT_BAR_CORNER_RADIUS)

    @Dimension
    private var _itemPadding = context.d2p(DEFAULT_ITEM_PADDING)

    private var _itemAnimDuration = DEFAULT_ANIM_DURATION

    @Dimension
    private var _itemIconSize = context.d2p(DEFAULT_ICON_SIZE)

    @Dimension
    private var _itemIconMargin = context.d2p(DEFAULT_ICON_MARGIN)

    @ColorInt
    private var _itemIconTint = Color.parseColor(DEFAULT_TINT)

    @ColorInt
    private var _itemIconTintActive = Color.WHITE

    @ColorInt
    private var _itemTextColor = Color.WHITE

    @Dimension
    private var _itemTextSize = context.d2p(DEFAULT_TEXT_SIZE)

    @FontRes
    private var _itemFontFamily: Int = INVALID_RES

    @XmlRes
    private var _itemMenuRes: Int = INVALID_RES

    private var _itemActiveIndex: Int = 0


    // Core Attributes
    var barBackgroundColor: Int
        @ColorInt get() = _barBackgroundColor
        set(@ColorInt value) {
            _barBackgroundColor = value
            paintBackground.color = value
            invalidate()
        }

    var barIndicatorColor: Int
        @ColorInt get() = _barIndicatorColor
        set(@ColorInt value) {
            _barIndicatorColor = value
            paintIndicator.color = value
            invalidate()
        }

    var barIndicatorRadius: Float
        @Dimension get() = _barIndicatorRadius
        set(@Dimension value) {
            _barIndicatorRadius = value
            invalidate()
        }

    var barSideMargins: Float
        @Dimension get() = _barSideMargins
        set(@Dimension value) {
            _barSideMargins = value
            invalidate()
        }

    var barCornerRadius: Float
        @Dimension get() = _barCornerRadius
        set(@Dimension value) {
            _barCornerRadius = value
            invalidate()
        }

    var itemTextSize: Float
        @Dimension get() = _itemTextSize
        set(@Dimension value) {
            _itemTextSize = value
            paintText.textSize = value
            invalidate()
        }

    var itemTextColor: Int
        @ColorInt get() = _itemTextColor
        set(@ColorInt value) {
            _itemTextColor = value
            paintText.color = value
            invalidate()
        }

    var itemPadding: Float
        @Dimension get() = _itemPadding
        set(@Dimension value) {
            _itemPadding = value
            invalidate()
        }

    var itemAnimDuration: Long
        get() = _itemAnimDuration
        set(value) {
            _itemAnimDuration = value
        }

    var itemIconSize: Float
        @Dimension get() = _itemIconSize
        set(@Dimension value) {
            _itemIconSize = value
            invalidate()
        }

    var itemIconMargin: Float
        @Dimension get() = _itemIconMargin
        set(@Dimension value) {
            _itemIconMargin = value
            invalidate()
        }

    var itemIconTint: Int
        @ColorInt get() = _itemIconTint
        set(@ColorInt value) {
            _itemIconTint = value
            invalidate()
        }

    var itemIconTintActive: Int
        @ColorInt get() = _itemIconTintActive
        set(@ColorInt value) {
            _itemIconTintActive = value
            invalidate()
        }

    var itemFontFamily: Int
        @FontRes get() = _itemFontFamily
        set(@FontRes value) {
            _itemFontFamily = value
            if (value != INVALID_RES) {
                paintText.typeface = ResourcesCompat.getFont(context, value)
                invalidate()
            }
        }

    var itemMenuRes: Int
        @XmlRes get() = _itemMenuRes
        set(@XmlRes value) {
            _itemMenuRes = value
            if (value != INVALID_RES) {
                items = BottomBarParser(context, value).parse()
                invalidate()
            }
        }

    var itemActiveIndex: Int
        get() = _itemActiveIndex
        set(value) {
            _itemActiveIndex = value
            applyItemActiveIndex()
        }

    private lateinit var mDragHelpers: ViewDragHelper

    // Listeners
    var onItemSelectedListener: OnItemSelectedListener? = null

    var onItemReselectedListener: OnItemReselectedListener? = null

    var onItemSelected: ((Int) -> Unit)? = null

    var onItemReselected: ((Int) -> Unit)? = null

    // Paints
    private val paintBackground = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = barIndicatorColor
    }

    private val paintIndicator = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = barIndicatorColor
    }

    private val paintText = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemTextColor
        textSize = itemTextSize
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    init {
        val typedArray = ctx.obtainStyledAttributes(
            attrs,
            R.styleable.BottomToolbar
        )

        try {
            barBackgroundColor = typedArray.getColor(
                R.styleable.BottomToolbar_backgroundColor,
                barBackgroundColor
            )

            barIndicatorColor = typedArray.getColor(
                R.styleable.BottomToolbar_indicatorColor,
                barIndicatorColor
            )

            barIndicatorRadius = typedArray.getDimension(
                R.styleable.BottomToolbar_indicatorRadius,
                barIndicatorRadius
            )

            barSideMargins = typedArray.getDimension(
                R.styleable.BottomToolbar_sideMargins,
                barSideMargins
            )

            barCornerRadius = typedArray.getDimension(
                R.styleable.BottomToolbar_cornerRadius,
                barCornerRadius
            )

            itemPadding = typedArray.getDimension(
                R.styleable.BottomToolbar_itemPadding,
                itemPadding
            )

            itemTextColor = typedArray.getColor(
                R.styleable.BottomToolbar_textColor,
                itemTextColor
            )

            itemTextSize = typedArray.getDimension(
                R.styleable.BottomToolbar_textSize,
                itemTextSize
            )

            itemIconSize = typedArray.getDimension(
                R.styleable.BottomToolbar_iconSize,
                itemIconSize
            )

            itemIconMargin = typedArray.getDimension(
                R.styleable.BottomToolbar_iconMargin,
                itemIconMargin
            )

            itemIconTint = typedArray.getColor(
                R.styleable.BottomToolbar_iconTint,
                itemIconTint
            )

            itemIconTintActive = typedArray.getColor(
                R.styleable.BottomToolbar_iconTintActive,
                itemIconTintActive
            )

            itemActiveIndex = typedArray.getInt(
                R.styleable.BottomToolbar_activeItem,
                itemActiveIndex
            )

            itemFontFamily = typedArray.getResourceId(
                R.styleable.BottomToolbar_itemFontFamily,
                itemFontFamily
            )

            itemAnimDuration = typedArray.getInt(
                R.styleable.BottomToolbar_duration,
                itemAnimDuration.toInt()
            ).toLong()

            itemMenuRes = typedArray.getResourceId(
                R.styleable.BottomToolbar_menu,
                itemMenuRes
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var lastX = barSideMargins
        itemWidth = (width - (barSideMargins * 2)) / items.size

        // reverse items layout order if layout direction is RTL
        val itemsToLayout = items.reversed()

        for (item in itemsToLayout) {
            // Prevent text overflow by shortening the item title
            var shorted = false
            while (paintText.measureText(item.title) > itemWidth - itemIconSize - itemIconMargin - (itemPadding * 2)) {
                item.title = item.title.dropLast(1)
                shorted = true
            }

            // Add ellipsis character to item text if it is shorted
            if (shorted) {
                item.title = item.title.dropLast(1)
                item.title += context.getString(R.string.ellipsis)
            }

            item.rect = RectF(lastX, 0f, itemWidth + lastX, height.toFloat())
            lastX += itemWidth
        }

        // Set initial active item
        applyItemActiveIndex()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        if (barCornerRadius > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(
                0f, 0f,
                width.toFloat(),
                height.toFloat(),
                barCornerRadius,
                barCornerRadius,
                paintBackground
            )
        } else {
            canvas.drawRect(
                0f, 0f,
                width.toFloat(),
                height.toFloat(),
                paintBackground
            )
        }

        // Draw indicator
        rect.left = indicatorLocation
        rect.top = items[itemActiveIndex].rect.centerY() - itemIconSize / 2 - itemPadding
        rect.right = indicatorLocation + itemWidth
        rect.bottom = items[itemActiveIndex].rect.centerY() + itemIconSize / 2 + itemPadding

        canvas.drawRoundRect(
            rect,
            barIndicatorRadius,
            barIndicatorRadius,
            paintIndicator
        )

        val textHeight = (paintText.descent() + paintText.ascent()) / 2

        for ((index, item) in items.withIndex()) {
            val textLength = paintText.measureText(item.title)
            item.icon.mutate()
            item.icon.setBounds(
                item.rect.centerX()
                    .toInt() - itemIconSize.toInt() / 2 + ((textLength / 2) * (1 - (OPAQUE - item.alpha) / OPAQUE.toFloat())).toInt(),
                height / 2 - itemIconSize.toInt() / 2,
                item.rect.centerX()
                    .toInt() + itemIconSize.toInt() / 2 + ((textLength / 2) * (1 - (OPAQUE - item.alpha) / OPAQUE.toFloat())).toInt(),
                height / 2 + itemIconSize.toInt() / 2
            )

            tintAndDrawIcon(item, index, canvas)

            paintText.alpha = item.alpha
            canvas.drawText(
                item.title,
                item.rect.centerX() - (itemIconSize / 2 + itemIconMargin),
                item.rect.centerY() - textHeight, paintText
            )
        }

    }

    private fun tintAndDrawIcon(
        item: BottomBarItem,
        index: Int,
        canvas: Canvas
    ) {
        DrawableCompat.setTint(
            item.icon,
            if (index == itemActiveIndex) currentIconTint else itemIconTint
        )

        item.icon.draw(canvas)
    }

    fun setParallaxListener() {

    }

    /**
     * Handle item clicks
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && abs(event.downTime - event.eventTime) < 500) {
            for ((i, item) in items.withIndex()) {
                if (item.rect.contains(event.x, event.y)) {
                    if (i != itemActiveIndex) {
                        itemActiveIndex = i
                        onItemSelected?.invoke(i)
                        onItemSelectedListener?.onItemSelect(i)
                    } else {
                        onItemReselected?.invoke(i)
                        onItemReselectedListener?.onItemReselect(i)
                    }
                }
            }
        }

        return true
    }

    private fun applyItemActiveIndex() {
        if (items.isNotEmpty()) {
            for ((index, item) in items.withIndex()) {
                if (index == itemActiveIndex) {
                    animateAlpha(item, OPAQUE)
                } else {
                    animateAlpha(item, TRANSPARENT)
                }
            }

            ValueAnimator.ofFloat(
                indicatorLocation,
                items[itemActiveIndex].rect.left
            ).apply {
                duration = itemAnimDuration
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    indicatorLocation = animation.animatedValue as Float
                }
                start()
            }

            ValueAnimator.ofObject(ArgbEvaluator(), itemIconTint, itemIconTintActive).apply {
                duration = itemAnimDuration
                addUpdateListener {
                    currentIconTint = it.animatedValue as Int
                }
                start()
            }
        }
    }

    private fun animateAlpha(item: BottomBarItem, to: Int) {
        ValueAnimator.ofInt(item.alpha, to).apply {
            duration = itemAnimDuration
            addUpdateListener {
                val value = it.animatedValue as Int
                item.alpha = value
                invalidate()
            }
            start()
        }
    }

    fun setupWithNavController(menu: Menu, navController: NavController) {
        NavigationComponentHelper.setupWithNavController(menu, this, navController)
    }

    /**
     * Created by Vladislav Perevedentsev on 29.07.2020.
     *
     * Just call [setOnItemSelectedListener] to override [onItemSelectedListener]
     *
     * @sample
     * setOnItemSelectedListener { position ->
     * }
     */
    fun setOnItemSelectedListener(listener: (position: Int) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelect(pos: Int): Boolean {
                listener.invoke(pos)
                return true
            }
        }
    }

    /**
     * Created by Vladislav Perevedentsev on 29.07.2020.
     *
     * Just call [setOnItemReselectedListener] to override [onItemReselectedListener]
     *
     * @sample
     * setOnItemReselectedListener { position ->
     *     //TODO: Something
     * }
     */
    fun setOnItemReselectedListener(listener: (position: Int) -> Unit) {
        onItemReselectedListener = object : OnItemReselectedListener {
            override fun onItemReselect(pos: Int) {
                listener.invoke(pos)
            }
        }
    }

    companion object {
        private const val INVALID_RES = -1
        private const val DEFAULT_INDICATOR_COLOR = "#2DFFFFFF"
        private const val DEFAULT_TINT = "#C8FFFFFF"

        private const val DEFAULT_SIDE_MARGIN = 10f
        private const val DEFAULT_ITEM_PADDING = 10f
        private const val DEFAULT_ANIM_DURATION = 200L
        private const val DEFAULT_ICON_SIZE = 18F
        private const val DEFAULT_ICON_MARGIN = 4F
        private const val DEFAULT_TEXT_SIZE = 11F
        private const val DEFAULT_CORNER_RADIUS = 20F
        private const val DEFAULT_BAR_CORNER_RADIUS = 0F

        private const val OPAQUE = 255
        private const val TRANSPARENT = 0

        // BottomBarParser Define
        private const val ITEM_TAG = "item"
        private const val ICON_ATTRIBUTE = "icon"
        private const val TITLE_ATTRIBUTE = "title"

        fun Context.d2p(dp: Float): Float {
            return (dp * resources.displayMetrics.density).roundToInt().toFloat()
        }
    }

    inner class BottomBarParser(private val context: Context, @XmlRes res: Int) {

        private val parser: XmlResourceParser = context.resources.getXml(res)

        fun parse(): List<BottomBarItem> {
            val items: MutableList<BottomBarItem> = mutableListOf()
            var eventType: Int?

            do {
                eventType = parser.next()
                if (eventType == XmlResourceParser.START_TAG && parser.name == ITEM_TAG) {
                    items.add(getTabConfig(parser))
                }
            } while (eventType != XmlResourceParser.END_DOCUMENT)

            return items
        }

        private fun getTabConfig(parser: XmlResourceParser): BottomBarItem {
            val attributeCount = parser.attributeCount
            var itemText: String? = null
            var itemDrawable: Drawable? = null

            for (index in 0 until attributeCount) {
                when (parser.getAttributeName(index)) {
                    ICON_ATTRIBUTE ->
                        itemDrawable = ContextCompat.getDrawable(
                            context,
                            parser.getAttributeResourceValue(index, 0)
                        )

                    TITLE_ATTRIBUTE -> {
                        itemText = try {
                            context.getString(parser.getAttributeResourceValue(index, 0))
                        } catch (notFoundException: Resources.NotFoundException) {
                            parser.getAttributeValue(index)
                        }
                    }
                }
            }

            if (itemDrawable == null)
                throw Throwable("Item icon can not be null!")

            return BottomBarItem(itemText ?: "", itemDrawable, alpha = 0)
        }
    }

    /**
     * ToolbarLifecycleObserver Listener
     */
    inner class ToolbarLifecycleObserver(val activity: AppCompatActivity) : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume(){
            // 각 페이지에 맞게 툴바 레이아웃 세팅.
            if (activity is MainActivity) {
                itemActiveIndex = ToolBarDefine.POS_HOME
            } else if (activity is MemoDetailActivity) {
                itemActiveIndex = ToolBarDefine.POS_ADD
            }
        }
    }
}