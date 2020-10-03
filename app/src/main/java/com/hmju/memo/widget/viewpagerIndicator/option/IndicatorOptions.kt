package com.hmju.memo.widget.viewpagerIndicator.option

import com.hmju.memo.R
import com.hmju.memo.utils.ResourceProvider
import com.hmju.memo.widget.viewpagerIndicator.annotation.AIndicatorSlideMode
import com.hmju.memo.widget.viewpagerIndicator.annotation.AIndicatorStyle
import com.hmju.memo.widget.viewpagerIndicator.annotation.IndicatorSlideMode
import com.hmju.memo.widget.viewpagerIndicator.annotation.IndicatorStyle
import org.koin.core.KoinComponent
import org.koin.core.inject

class IndicatorOptions : KoinComponent {

    @AIndicatorStyle
    var indicatorStyle: Int = 0

    @AIndicatorSlideMode
    var slideMode: Int = 0

    var pageSize: Int = 0
    var normalSliderColor: Int = 0
        private set
    var checkedSliderColor: Int = 0
        private set
    var sliderGap: Float = 0.toFloat()

    var sliderHeight: Float = 0.toFloat()
        get() = if (field > 0) field else normalSliderWidth / 2

    var normalSliderWidth: Float = 0.toFloat()
        private set

    var checkedSliderWidth: Float = 0.toFloat()
        private set

    var currentPosition: Int = 0
    var slideProgress: Float = 0.toFloat()

    init {
        // 초기값 세팅.
        val res: ResourceProvider by inject()
        normalSliderWidth = res.getDimen(R.dimen.size_20).toFloat()
        checkedSliderWidth = normalSliderWidth
        sliderHeight = res.getDimen(R.dimen.size_5).toFloat()
        sliderGap = res.getDimen(R.dimen.size_5).toFloat()
        normalSliderColor = res.getColor(R.color.colorNormalIndicator)
        checkedSliderColor = res.getColor(R.color.colorCheckedIndicator)
        slideMode = IndicatorSlideMode.SMOOTH
        indicatorStyle = IndicatorStyle.ROUND_RECT
    }
}