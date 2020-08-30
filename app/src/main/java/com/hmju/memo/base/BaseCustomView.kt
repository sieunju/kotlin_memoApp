package com.hmju.memo.base

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import org.koin.core.KoinComponent

/**
 * Description :
 *
 * Created by juhongmin on 2020/08/16
 */
abstract class BaseCustomView<T : ViewDataBinding>(
    private val ctx: Context,
    private val attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs), KoinComponent {

    constructor(ctx: Context) : this(ctx, null)

    private fun getAttrs(attrs: AttributeSet?) {
        setTypeArray(context.obtainStyledAttributes(attrs, getCustomViewStyle()))
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int = 0) {
        setTypeArray(
            context.obtainStyledAttributes(
                attrs,
                getCustomViewStyle(),
                defStyle,
                defStyleRes
            )
        )
    }

    init {
//        DataBindingUtil.bind<T>(LayoutInflater.from(context).inflate(layoutId,this,false).apply {
//            addView(this)
//        })

    }

    abstract fun setTypeArray(typedArray: TypedArray)
    abstract val layoutId: Int
    abstract fun getCustomViewStyle(): IntArray
    protected lateinit var binding: T
}