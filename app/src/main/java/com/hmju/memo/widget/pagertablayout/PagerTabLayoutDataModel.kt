package com.hmju.memo.widget.pagertablayout

import android.content.res.ColorStateList
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import com.hmju.memo.convenience.NonNullMutableLiveData

data class PagerTabLayoutDataModel(
        val title: String,
        @DrawableRes val icon: Int? = null,
        var pos: Int = 0,
        val isSelected: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false),
        var textColorStyle : ColorStateList? = null,
        @Dimension var iconWidth : Int? = null
)