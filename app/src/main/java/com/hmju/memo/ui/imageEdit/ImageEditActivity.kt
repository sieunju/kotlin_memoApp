package com.hmju.memo.ui.imageEdit

import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityImageEditBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.ImageEditViewModel
import com.hmju.memo.widget.pitchZoomImageView.FlexibleImageView
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
        parametersOf(intent.getStringExtra(ExtraCode.IMAGE_EDIT_PHOTO_URI))
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

            startCropImage.observe(this@ImageEditActivity, Observer {
                JLogger.d("Crop Image Bitmap Size ${it.byteCount} Width ${it.width} Height ${it.height}")
                cropImgView.setImageBitmap(it)
            })
        }
    }
}