package com.hmju.memo.ui.album

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.bumptech.glide.load.resource.gif.StreamGifDecoder
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityAlbumBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
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

            startCamera.observe(this@AlbumActivity, Observer {
                JLogger.d("카메라 촬영 시작!")
            })

            startSubmit.observe(this@AlbumActivity, Observer {
                finish()
            })

            startToast.observe(this@AlbumActivity, Observer { msg->
                showToast(msg)
            })

            start()
        }
    }

    override fun finish() {
        with(viewModel) {
            if (selectedPhotoList.isNotEmpty()) {
                JLogger.d("데이터 가공!")
                val intent = Intent()
                val list = arrayListOf<String>()
                selectedPhotoList.forEach {
                    list.add(it.value)
                }
                intent.putStringArrayListExtra(ExtraCode.ALBUM_SELECT_IMAGES, list)
                setResult(RESULT_OK, intent)
            } else {
                setResult(RESULT_CANCELED)
            }
            super.finish()
        }
    }
}