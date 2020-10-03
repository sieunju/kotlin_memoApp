package com.hmju.memo.widget.viewpagerIndicator.annotation

import androidx.annotation.IntDef
interface IndicatorSlideMode {
    companion object {
        const val NORMAL = 0
        const val SMOOTH = 2
        const val WORM = 3
        const val SCALE = 4
        const val COLOR = 5
    }
}

@IntDef(
    IndicatorSlideMode.NORMAL,
    IndicatorSlideMode.SMOOTH,
    IndicatorSlideMode.WORM,
    IndicatorSlideMode.COLOR,
    IndicatorSlideMode.SCALE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class AIndicatorSlideMode
