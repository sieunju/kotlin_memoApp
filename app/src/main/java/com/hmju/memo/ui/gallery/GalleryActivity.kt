package com.hmju.memo.ui.gallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityGalleryBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ResultCode
import com.hmju.memo.dialog.CommonDialog
import com.hmju.memo.ui.imageEdit.ImageEditActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveCamera
import com.hmju.memo.utils.startAct
import com.hmju.memo.viewmodels.GalleryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.parameter.parametersOf
import java.io.File


/**
 * Description : 앨범 및 카메라 페이지
 *
 * Created by hmju on 2020-06-16
 */
class GalleryActivity : BaseActivity<ActivityGalleryBinding, GalleryViewModel>(), KoinComponent {
    override val layoutId = R.layout.activity_gallery

    override val viewModel: GalleryViewModel by viewModel {
        parametersOf(intent.getIntExtra(ExtraCode.GALLERY_IMG_LIMIT, 0))
    }

    override val bindingVariable = BR.viewModel

    //    private lateinit var selectDialog: CheckableBottomSheet
    private lateinit var selectedFilterDialog: SelectedFilterBottomSheet

    private var photoUri: Uri? = null
    private val imgEditListTest = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 체크
        with(RxPermissions(this@GalleryActivity)) {
            request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe { isGranted ->
                if (!isGranted) {
                    CommonDialog(this@GalleryActivity)
                        .setContents(R.string.str_permission_denied)
                        .setPositiveButton(R.string.str_confirm)
                        .setListener(object : CommonDialog.Listener {
                            override fun onClick(which: Int) {
                                finish()
                            }
                        })
                        .show()
                }
            }
        }

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
                            val file = fileProvider.createTempFile()
                            photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                FileProvider.getUriForFile(this@GalleryActivity, packageName, file)
                            } else {
                                Uri.fromFile(file)
                            }

                            moveCamera(photoUri)

                        } else {
                            // 권한 확인 안내 팝업 노출
                            CommonDialog(this@GalleryActivity)
                                .setContents(R.string.str_permission_denied)
                                .setPositiveButton(R.string.str_confirm)
                                .show()
                        }
                    }
                }
            })

            startSubmit.observe(this@GalleryActivity, Observer {
                if (selectedPhotoList.size() > 0) {
                    val intent = Intent()
                    intent.putStringArrayListExtra(
                        ExtraCode.GALLERY_SELECT_IMAGES,
                        selectedPhotoList.value
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
                selectedFilterDialog = SelectedFilterBottomSheet.newInstance(
                    filterList.value
                ) { item ->
                    if (selectedFilter(item)) {
                        fetchGallery()
                    }
                    selectedFilterDialog.dismiss()
                }.also {
                    it.show(supportFragmentManager, "selectedFilterDialog")
                }
            })

            // TEST 를 위한 코드.
            startImageEdit.observe(this@GalleryActivity, Observer { uri ->
                imgEditListTest.add(uri)
                // 데이터가 2개 쌓였을때 실행.
                if (imgEditListTest.size == 2) {
                    startAct<ImageEditActivity> {
                        putStringArrayListExtra(ExtraCode.IMAGE_EDIT_PHOTO_URIS, imgEditListTest)
                    }
                    imgEditListTest.clear()
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
                    JLogger.d("촬영 성공 성공 $photoUri")

                    Intent().apply {
                        putExtra(ExtraCode.CAMERA_CAPTURE_PHOTO_URI, photoUri.toString())
                        setResult(ResultCode.CAMERA_CAPTURE_OK, this)
                        finish()
                    }
                } else {
                    JLogger.d("카메라 캡처 취소..")
                    // Remove Photo
                    photoUri?.let {
                        File(it.toString()).delete()
                    }
                }
            }
        }
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun galleryAddPic() {
//        val values = ContentValues().apply {
//            put(
//                MediaStore.Images.Media.DISPLAY_NAME,
//                "MemoApp_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
//            )
//            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                put(MediaStore.Images.Media.IS_PENDING, 1)
//            }
//        }
//
//        val item: Uri? =
//            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        try {
//            item?.let { uri ->
//                val pdf = contentResolver.openFileDescriptor(uri, "w", null)
//                pdf?.let {
//                    val tempFile = File(photoUri!!)
//                    val fos = FileOutputStream(it.fileDescriptor)
//                    fos.write(tempFile.readBytes())
//                    fos.close()
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        values.clear()
//                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
//                        contentResolver.update(uri, values, null, null)
//
//                        // 해당 파일 삭제.
//                        tempFile.delete()
//                    }
//                } ?: {
//                    JLogger.d("PDF Null")
//                }()
//            }
//        } catch (ex: Exception) {
//            JLogger.d("Error ${ex.message}")
//        }
//    }
}