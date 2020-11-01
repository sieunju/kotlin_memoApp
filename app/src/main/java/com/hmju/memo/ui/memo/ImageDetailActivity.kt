package com.hmju.memo.ui.memo

import android.os.Bundle
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityImageDetailBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.viewmodels.ImageDetailViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 이미지 자세히 보기 페이지.
 *
 * Created by hmju on 2020-11-01
 */
class ImageDetailActivity : BaseActivity<ActivityImageDetailBinding, ImageDetailViewModel>() {

    override val layoutId = R.layout.activity_image_detail

    override val viewModel: ImageDetailViewModel by viewModel {
        parametersOf(
            intent.getIntExtra(ExtraCode.IMAGE_DETAIL_POS, 0),
            intent.getSerializableExtra(ExtraCode.IMAGE_DETAIL_PATH_LIST)
        )
    }
    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}