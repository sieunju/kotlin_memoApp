package com.hmju.memo.ui.gallery

import android.Manifest
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityGalleryBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ResultCode
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.ui.bottomsheet.CheckableBottomSheet
import com.hmju.memo.ui.imageEdit.ImageEditActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveCameraCapture
import com.hmju.memo.utils.startAct
import com.hmju.memo.viewModels.GalleryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_gallery.*
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
        parametersOf(intent.getIntExtra(ExtraCode.GALLERY_IMG_LIMIT, 0))
    }

    override val bindingVariable = BR.viewModel

    private lateinit var selectDialog: CheckableBottomSheet
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                with(RxPermissions(this@GalleryActivity)) {
                    request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ).subscribe { isGranted ->
                        if (isGranted) {
                            moveCameraCapture { callbackUri ->
                                photoUri = callbackUri
                            }
                        } else {
                            // 권한 확인 안내 팝업 노출
                            ConfirmDialog(this@GalleryActivity, R.string.str_permission_denied)
                        }
                    }
                }

            })

            startSubmit.observe(this@GalleryActivity, Observer {
                if (selectedPhotoList.size() > 0) {
                    val intent = Intent()
                    val list = selectedPhotoList.value.map { it.id }.toList()
                    val arrayList = arrayListOf<String>()
                    arrayList.addAll(list)
                    intent.putStringArrayListExtra(
                        ExtraCode.GALLERY_SELECT_IMAGES,
                        arrayList
                    )
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
                val list = arrayListOf<CheckableBottomSheet.CheckableBottomSheetItem>()
                filterList.value.forEach {
                    list.add(
                        CheckableBottomSheet.CheckableBottomSheetItem(
                            id = it.id,
                            name = it.name,
                            isSelected = it.isSelected
                        )
                    )
                }

                selectDialog = CheckableBottomSheet.newInstance(
                    resources.getDimensionPixelOffset(R.dimen.size_200),
                    list
                ) { pos, id ->
                    startNetworkState.postValue(NetworkState.LOADING)
                    resetFilter()
                    selectedFilter(id)

                    fetchGallery()
                    selectDialog.dismiss()
                }.also {
                    it.show(supportFragmentManager, "filterDialog")
                }
            })

            startImageEdit.observe(this@GalleryActivity, Observer { uri ->
                startAct<ImageEditActivity> {
                    putExtra(ExtraCode.IMAGE_EDIT_PHOTO_URI, uri)
                }
            })

            startNotify.observe(this@GalleryActivity, Observer { item ->
                rvContents.adapter?.let {
                    it.notifyItemChanged(item.pos + 1)
                }
            })

            start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.CAMERA_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    savePicture()
                    val intent = Intent()
                    intent.putExtra(ExtraCode.CAMERA_CAPTURE_PHOTO_URI, photoUri.toString())
                    setResult(ResultCode.CAMERA_CAPTURE_OK, intent)
                    finish()
                } else {
                    // Remove Photo
                    photoUri?.let {
                        contentResolver.delete(it, null, null)
                        JLogger.d("취소!")
                    }
                }
            }
        }
    }

    private fun savePicture() {
        try {
            MediaScannerConnection.scanFile(
                this,
                arrayOf(photoUri.toString()),
                null
            ) { path, uri ->
                JLogger.d("Path $path Uri $uri")
            }
        } catch (ex: Exception) {

        }
    }
}