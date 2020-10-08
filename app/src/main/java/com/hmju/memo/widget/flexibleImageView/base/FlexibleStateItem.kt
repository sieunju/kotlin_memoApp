package com.hmju.memo.widget.flexibleImageView.base

/**
 * Description: Flexible State Data Class
 * 각 초기값은
 * Scale 1.0F
 * Focus 0F
 * Rotate 0F
 * Flip 1F
 * Created by hmju on 2020-10-08
 */
data class FlexibleStateItem(
    var scale: Float,
    var focusX: Float,
    var focusY: Float,
    var rotationDegree: Float,
    var flipX: Float,
    var flipY: Float
) {

    val scaleX: Float
        get() = scale * flipX
    val scaleY: Float
        get() = scale * flipY

    fun reset() {
        scale = 1.0F
        focusX = 0F
        focusY = 0F
        rotationDegree = 0F
        flipX = 1F
        flipY = 1F
    }

//    fun scaleX(): Float {
//        return scale * flipX
//    }
//
//    fun scaleY(): Float {
//        return scale * flipY
//    }
}