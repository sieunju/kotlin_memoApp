package com.hmju.memo.ui.toast

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import com.hmju.memo.R
import kotlinx.android.synthetic.main.toast_view.view.*

/**
 * Description :
 *
 * Created by hmju on 2020-09-09
 */

inline fun Toast.showToast(act : Activity,text: String, duration: Int) {
    val inflater = act.layoutInflater
    val view = inflater.inflate(R.layout.toast_view,act as ViewGroup)
    view.tvToast.text = text

    val toast = Toast(act)
    toast.setGravity(Gravity.CENTER,0,0)
    toast.duration = duration
    toast.view = view
    toast.show()
}