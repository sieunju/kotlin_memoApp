package com.hmju.memo.ui.memo

import android.Manifest
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoAddBinding
import com.hmju.memo.define.*
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.ui.bottomsheet.MemoMoreDialog
import com.hmju.memo.ui.gallery.GalleryActivity
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MemoAddViewModel
import com.hmju.memo.widget.keyboard.FluidContentResize
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_memo_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Description : 메인 추가 페이지
 *
 * Created by hmju on 2020-06-16
 */
class MemoAddActivity : BaseActivity<ActivityMemoAddBinding, MemoAddViewModel>() {

    override val layoutId = R.layout.activity_memo_add
    override val viewModel: MemoAddViewModel by viewModel()
    override val bindingVariable = BR.viewModel
    private lateinit var moreDialog: MemoMoreDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 자연스러운 키보드 올라오기 위한 코드.
        FluidContentResize.listen(this)

        with(viewModel) {

            startSelectedTagColor.observe(this@MemoAddActivity, Observer { color ->
                // 상태바 색상 변경.
                window.statusBarColor = color
                vSelectedTag.setBackgroundColor(color)
                ObjectAnimator.ofFloat(vSelectedTag, View.ALPHA, 0.25F, 1.0F).apply {
                    duration = 500
                    start()
                }
            })

            startNetworkState.observe(this@MemoAddActivity, Observer { state ->
                JLogger.d("Network State $state")
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

            startDialog.observe(this@MemoAddActivity, Observer { msg ->
                ConfirmDialog(this@MemoAddActivity, msg)
            })

            startMoreDialog.observe(this@MemoAddActivity, Observer {
                moreDialog = MemoMoreDialog.newInstance(viewModel) { type, pos ->
                    // 일반 선택
                    if (type == MemoMoreDialog.Type.NORMAL.type) {
                        when (pos) {
                            0 -> {
                                // 사진 추가.
                                moveGallery()
                            }
                            1 -> {
                                // 메모 삭제.
                                ConfirmDialog(
                                    ctx = this@MemoAddActivity,
                                    msg = resources.getString(R.string.memo_delete_guide),
                                    leftText = resources.getString(R.string.str_cancel),
                                    rightText = resources.getString(R.string.str_confirm)
                                ) { _, which ->
                                    if (which == DialogInterface.BUTTON_POSITIVE) {

                                    }
                                }
                            }
                        }
                        moreDialog.dismiss()
                    }
                }.also {
                    it.show(supportFragmentManager, "moreDialog")
                }
            })

            // 갤러리 페이지 진입.
            startGallery.observe(this@MemoAddActivity, Observer {
                with(RxPermissions(this@MemoAddActivity)) {
                    request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe { isGranted ->
                        // 동의 한경우.
                        if (isGranted) {
                            startActResult<GalleryActivity>(com.hmju.memo.define.RequestCode.GALLERY) {
                                putExtra(
                                    com.hmju.memo.define.ExtraCode.GALLERY_IMG_LIMIT,
                                    0.coerceAtLeast(Etc.IMG_FILE_LIMIT - fileSize.value!!)
                                )
                            }
                        } else {
                            // 권한 확인 안내 팝업 노출
                            ConfirmDialog(this@MemoAddActivity, R.string.str_permission_denied)
                        }
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.GALLERY -> {
                when (resultCode) {
                    RESULT_OK -> {
                        // 갤러리에서 사진 가져온 경우.
                        with(viewModel) {
                            data?.getStringArrayListExtra(ExtraCode.GALLERY_SELECT_IMAGES)?.let {
                                addFileUpload(it)
                            }
                        }
                    }

                    ResultCode.CAMERA_CAPTURE_OK -> {
                        // 카메라 캡처에서 사진 가져온 경우.
                        data?.getStringExtra(ExtraCode.CAMERA_CAPTURE_PHOTO_URI)?.let {
                            with(viewModel) {
                                JLogger.d("Photo Uri $it")
                                addFileUpload(arrayListOf(it))
                            }
                        }
                    }
                    else -> {

                    }
                }

            }
        }
    }
}