package com.hmju.memo.ui.gallery

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityGalleryBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.ui.bottomsheet.SelectBottomSheet
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.GalleryViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 앨범 및 카메라 페이지
 *
 * Created by hmju on 2020-06-16
 */
class GalleryActivity : BaseActivity<ActivityGalleryBinding, GalleryViewModel>() {
    override val layoutId = R.layout.activity_gallery

    override val viewModel: GalleryViewModel by viewModel {
        parametersOf(intent.getIntExtra(ExtraCode.ALBUM_MANAGE_NO, -1))
    }

    override val bindingVariable = BR.viewModel

    private lateinit var selectDialog: SelectBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // StatusBar 까지 영역 표시
//        setFitsWindows()

        with(viewModel) {

            startNetworkState.observe(this@GalleryActivity, Observer { state ->
                when (state) {
                    NetworkState.LOADING -> {
                        showLoadingDialog()
                    }
                    NetworkState.ERROR -> {
                        dismissLoadingDialog()
                    }
                    else -> {
                        dismissLoadingDialog()
                    }
                }
            })

            startCamera.observe(this@GalleryActivity, Observer {
                JLogger.d("카메라 촬영 시작!")
            })

            startSubmit.observe(this@GalleryActivity, Observer {
                if (selectedPhotoList.size() > 0) {
                    JLogger.d("데이터 가공!")
                    val intent = Intent()
                    intent.putStringArrayListExtra(ExtraCode.ALBUM_SELECT_IMAGES, selectedPhotoList.value)
                    setResult(RESULT_OK, intent)
                } else {
                    setResult(RESULT_CANCELED)
                }
                finish()
            })

            startToast.observe(this@GalleryActivity, Observer { msg ->
                showToast(msg)
            })

            startFilter.observe(this@GalleryActivity, Observer {
                val list = arrayListOf<SelectBottomSheet.BottomSheetSelect>()
                filterList.value.forEach {
                    list.add(
                        SelectBottomSheet.BottomSheetSelect(
                            id = it.id,
                            name = it.name,
                            isSelected = it.isSelected
                        )
                    )
                }

                selectDialog = SelectBottomSheet.newInstance(list) { pos, id ->
                    startNetworkState.postValue(NetworkState.LOADING)
                    JLogger.d("Selected $pos $id")
                    resetFilter()
                    selectedFilter(id)

                    fetchGallery()
                    selectDialog.dismiss()
                }.also {
                    it.show(supportFragmentManager, "")
                }

            })

            start()
        }
    }

    override fun finish() {
        super.finish()
    }
}