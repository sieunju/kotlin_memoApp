package com.hmju.memo.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

/**
 * Description : ResourceProvider
 *
 * Created by juhongmin on 2020/09/05
 */
interface ResourceProvider {
    fun getContext() : Context
    fun getDrawable(@DrawableRes resId: Int) : Drawable?
    fun getDimen(@DimenRes resId: Int) : Int
    fun getColor(@ColorRes color: Int) : Int
    fun getString(@StringRes resId : Int) : String
    fun getStringArray(@ArrayRes resId: Int) : Array<String>
    fun getContentResolver() : ContentResolver
}

class ResourceProviderImpl(private val ctx: Context) : ResourceProvider {
    private val res by lazy{ctx.resources}

    override fun getContext() = ctx

    override fun getDrawable(resId: Int) = AppCompatResources.getDrawable(ctx,resId)

    override fun getDimen(resId: Int) = res.getDimensionPixelSize(resId)

    override fun getColor(color: Int) = ContextCompat.getColor(ctx,color)

    override fun getString(resId: Int) = res.getString(resId)

    override fun getStringArray(resId: Int) = res.getStringArray(resId)

    override fun getContentResolver(): ContentResolver {
        return ctx.contentResolver
    }
}