package com.hmju.memo.ui.bindingadapter

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/07
 */


@BindingAdapter("userNameitem")
fun bindingUserName(
    view: AppCompatTextView,
    userName: String
) {
    view.text = userName
}




