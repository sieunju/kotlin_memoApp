package com.hmju.memo.widget.viewpagerIndicator.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.hmju.memo.utils.JLogger
import com.hmju.memo.widget.viewpagerIndicator.option.IndicatorOptions

/**
 * Description : Open Lib @link{https://github.com/zhpanvip/viewpagerindicator}
 * Style
 * ROUND_RECT
 * Slide Mode
 * SMOOTH 버전
 *
 * Created by juhongmin on 2020/10/02
 */
open class BaseIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(context, attrs, defStyleAttr), IndicatorListener {

    var mIndicatorOptions: IndicatorOptions

    private val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            pageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    var pageSize: Int = 0
        set(value) {
            // 페이지 값 변경되면 갱신 처리.
            field = value
            mIndicatorOptions.pageSize = value
            notifyDataChanged()
        }

    var slideProgress: Float
        get() = mIndicatorOptions.slideProgress
        private set(slideProgress) {
            mIndicatorOptions.slideProgress = slideProgress
        }

    var currentPosition: Int
        get() = mIndicatorOptions.currentPosition
        private set(currentPosition) {
            mIndicatorOptions.currentPosition = currentPosition
        }

    init {
        mIndicatorOptions = IndicatorOptions()
    }

    private fun pageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (pageSize > 1) {
            scrollSlider(position, positionOffset)
            invalidate()
        }
    }

    private fun scrollSlider(position: Int, positionOffset: Float) {

        if (position % pageSize == pageSize - 1) {
            if (positionOffset < 0.5) {
                currentPosition = position
                slideProgress = 0f
            } else {
                currentPosition = 0
                slideProgress = 0f
            }
        } else {
            currentPosition = position
            slideProgress = positionOffset
        }
    }

    fun setViewPager(viewPager: ViewPager2) {
        viewPager.apply {
            unregisterOnPageChangeCallback(mOnPageChangeCallback)
            registerOnPageChangeCallback(mOnPageChangeCallback)
            notifyDataChanged()
        }
    }

    override fun setIndicatorOptions(options: IndicatorOptions) {
        mIndicatorOptions = options
    }

    override fun notifyDataChanged() {
        requestLayout()
        invalidate()
    }
}