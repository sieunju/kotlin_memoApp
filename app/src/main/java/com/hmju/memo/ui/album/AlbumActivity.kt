package com.hmju.memo.ui.album

import android.os.Bundle
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityAlbumBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.viewModels.AlbumViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 앨범 및 카메라 페이지
 *
 * Created by hmju on 2020-06-16
 */
class AlbumActivity : BaseActivity<ActivityAlbumBinding, AlbumViewModel>() {
    override val layoutId = R.layout.activity_album

    override val viewModel: AlbumViewModel by viewModel {
        parametersOf(intent.getIntExtra(ExtraCode.ALBUM_MANAGE_NO, -1))
    }

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // StatusBar 까지 영역 표시
//        setFitsWindows()

        with(viewModel) {

            start()
        }
    }
}