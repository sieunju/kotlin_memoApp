package com.hmju.memo.ui.toast

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import com.hmju.memo.databinding.ToastViewBinding

fun Activity.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(
        text = getString(resId),
        duration = duration
    )
}

fun Activity.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    val inflater = layoutInflater
    val binding = ToastViewBinding.inflate(inflater)
    binding.tvToast.text = text

    val toast = Toast(this)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = duration
    toast.view = binding.root
    toast.show()
}