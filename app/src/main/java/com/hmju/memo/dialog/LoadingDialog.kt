package com.hmju.memo.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hmju.memo.R
import kotlinx.android.synthetic.main.dialog_loading.*

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