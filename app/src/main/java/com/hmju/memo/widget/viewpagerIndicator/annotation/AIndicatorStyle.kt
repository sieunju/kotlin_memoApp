package com.hmju.memo.widget.viewpagerIndicator.annotation

import androidx.annotation.IntDef

interface IndicatorStyle {
    companion object {
        const val CIRCLE = 0
        const val DASH = 1 shl 1
        const val ROUND_RECT = 1 shl 2
    }
}

@IntDef(IndicatorStyle.CIRCLE, IndicatorStyle.DASH, IndicatorStyle.ROUND_RECT)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class AIndicatorStyle
