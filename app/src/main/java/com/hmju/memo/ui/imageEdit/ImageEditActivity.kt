package com.hmju.memo.ui.imageEdit

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityImageEditBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.ImageEditViewModel
import kotlinx.android.synthetic.main.activity_image_edit.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 이미지 편집 페이지
 *
 * Created by hmju on 2020-05-10
 */
class ImageEditActivity : BaseActivity<ActivityImageEditBinding, ImageEditViewModel>() {

    override val layoutId = R.layout.activity_image_edit
    override val viewModel: ImageEditViewModel by viewModel {
        parametersOf(intent.getStringArrayListExtra(ExtraCode.IMAGE_EDIT_PHOTO_URIS))
    }

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {

            startNetworkState.observe(this@ImageEditActivity, Observer { state ->
                when (state) {
                    NetworkState.LOADING -> {
                        showLoadingDialog()
                    }
                    else -> {
                        dismissLoadingDialog()
                    }
                }
            })

            startResetImage.observe(this@ImageEditActivity, Observer {
                imgLeft.resetView()
                imgRight.resetView()
            })

            startSwitchImage.observe(this@ImageEditActivity, Observer {
                imgLeft.setImageBitmap(it.first)
                imgRight.setImageBitmap(it.second)
                ObjectAnimator.ofFloat(clLeft,View.ALPHA,0.25F,1F).apply {
                    duration = 1000
                    start()
                }
                ObjectAnimator.ofFloat(clRight,View.ALPHA,0.25F,1F).apply {
                    duration = 1000
                    start()
                }

            })
        }
    }
}