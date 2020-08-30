package com.hmju.memo.base

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.LayoutInflaterCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.hmju.memo.R
import com.hmju.memo.ui.bindingadapter.getUrl
import com.hmju.memo.utils.JLogger
import kotlinx.android.synthetic.main.layout_header_type_img_search.view.*
import org.koin.core.KoinComponent

/**
 * Description : Base HeaderView
 *
 * Created by hmju on 2020-06-12
 */
class HeaderView (private val ctx: Context, private val attrs: AttributeSet?)
    : CoordinatorLayout(ctx, attrs) , KoinComponent {

    enum class Type {
        NONE, TITLE, IMG, IMG_SEARCH
    }

    private var mType: Type? = Type.NONE

    constructor(ctx: Context) : this(ctx, null)

    init {
        if (isInEditMode) throw IllegalArgumentException("isInEditMode true...!!")

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        setLayoutParams(layoutParams)

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

    fun setThumbName(url : String) {
        Glide.with(context)
            .load(getUrl(context.getString(R.string.test_img)))
            .placeholder(R.color.colorAccent)
            .override(200,200)
            .transform(CenterCrop())
            .into(img_header)
    }
}