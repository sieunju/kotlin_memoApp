package com.hmju.memo.base

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.LayoutInflaterCompat
import com.hmju.memo.R
import com.hmju.memo.utils.JLogger

/**
 * Description : Base HeaderView
 *
 * Created by hmju on 2020-06-12
 */
class HeaderView : CoordinatorLayout {

    enum class Type {
        NONE, TITLE, IMG, IMG_SEARCH
    }

    private var mType: Type? = Type.NONE

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        if (isInEditMode) return

        // 속성 값 세팅
        attrs?.let {
            val attr: TypedArray = context.obtainStyledAttributes(it, R.styleable.HeaderView)

            // 타입 값 세팅
            mType = Type.values()[attr.getInt(R.styleable.HeaderView_header_type, 0)]
            attr.recycle()
        }

        // View 타입에 맞게 View 추가.
        @LayoutRes
        val layoutId = when (mType) {
            Type.TITLE -> {
                R.layout.layout_header_type_title
            }
            Type.IMG -> {
                R.layout.layout_header_type_img
            }
            Type.IMG_SEARCH -> {
                R.layout.layout_header_type_img_search
            }
            // None type.
            else -> {
                R.layout.layout_header_type_none
            }
        }

        val rootView = LayoutInflater.from(context).inflate(layoutId, this, false)
        addView(rootView)
    }
}