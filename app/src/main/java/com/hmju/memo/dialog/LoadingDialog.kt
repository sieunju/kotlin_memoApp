package com.hmju.memo.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import com.hmju.memo.R

/**
 * Description :
 *
 * Created by juhongmin on 2020/09/20
 */
class LoadingDialog(private val ctx: Context): AppCompatDialog(ctx) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        setOnShowListener {
        }

        setOnDismissListener {
            try {
                dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}