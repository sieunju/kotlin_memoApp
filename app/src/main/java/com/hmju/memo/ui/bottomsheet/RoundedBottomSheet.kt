package com.hmju.memo.ui.bottomsheet

import android.view.View
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
}