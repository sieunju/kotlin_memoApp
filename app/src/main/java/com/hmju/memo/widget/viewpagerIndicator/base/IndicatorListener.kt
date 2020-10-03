package com.hmju.memo.widget.viewpagerIndicator.base

import com.hmju.memo.widget.viewpagerIndicator.option.IndicatorOptions

interface IndicatorListener {

    fun notifyDataChanged()

    fun setIndicatorOptions(options: IndicatorOptions)
}