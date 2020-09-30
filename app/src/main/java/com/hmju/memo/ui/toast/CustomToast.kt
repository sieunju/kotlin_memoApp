package com.hmju.memo.ui.toast

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import com.hmju.memo.R
import kotlinx.android.synthetic.main.toast_view.*
import kotlinx.android.synthetic.main.toast_view.view.*

fun Activity.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(
        text = getString(resId),
        duration = duration
    )
}

fun Activity.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    val inflater = layoutInflater
    val view = inflater.inflate(R.layout.toast_view, toastDesignRoot)
    view.tvToast.text = text

    val toast = Toast(this)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = duration
    toast.view = view
    toast.show()
}