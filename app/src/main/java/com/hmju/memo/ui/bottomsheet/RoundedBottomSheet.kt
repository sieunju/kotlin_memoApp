package com.hmju.memo.ui.bottomsheet

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hmju.memo.R


/**
 * Description : Rouneded 효과 있는 BottomSheetFragment
 *
 * Created by juhongmin on 2020/09/06
 */
open class RoundedBottomSheet : BottomSheetDialogFragment() {

    override fun onStart() {
        super.onStart()
        view?.post {
            val parent = view?.parent as View
            context?.let {
                parent.background = ContextCompat.getDrawable(it, R.drawable.bg_round_bottom_sheet)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected fun setWhiteNavigationBar() {
        dialog?.window?.apply {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)

            val layers =
                arrayOf<Drawable>(dimDrawable, navigationBarDrawable)

            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)

            setBackgroundDrawable(windowBackground)
        }
    }
}