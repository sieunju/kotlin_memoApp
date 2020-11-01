package com.hmju.memo.ui.memo

import android.Manifest
import android.animation.ObjectAnimator
import android.content.*
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoDetailBinding
import com.hmju.memo.define.*
import com.hmju.memo.dialog.CommonDialog
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.bottomsheet.MemoMoreDialog
import com.hmju.memo.ui.gallery.GalleryActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MemoEditViewModel
import com.hmju.memo.widget.keyboard.FluidContentResize
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_memo_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 메인 자세히 보기 페이지
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailActivity : BaseActivity<ActivityMemoDetailBinding, MemoEditViewModel>() {


    override val layoutId = R.layout.activity_memo_detail
    override val viewModel: MemoEditViewModel by viewModel {
        parametersOf(
            intent.getSerializableExtra(ExtraCode.MEMO_DETAIL)
        )
    }

    override val bindingVariable = BR.viewModel

    private val memoPosition by lazy { intent.getIntExtra(ExtraCode.MEMO_DETAIL_POS,-1) }
    private lateinit var moreDialog: MemoMoreDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransFormationEndContainer()
        super.onCreate(savedInstanceState)
        // 자연스러운 키보드 올라오기 위한 코드.
        FluidContentResize.listen(this)

        with(viewModel) {

            initSelectedTag()

            startSelectedTagColor.observe(this@MemoDetailActivity, Observer { color ->
                // 상태바 색상 변경.
                window.statusBarColor = color
                vSelectedTag.setBackgroundColor(color)
                ObjectAnimator.ofFloat(vSelectedTag, View.ALPHA, 0.25F, 1.0F).apply {
                    duration = 500
                    start()
                }
            })

            startNetworkState.observe(this@MemoDetailActivity, Observer { state ->
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

            startDialog.observe(this@MemoDetailActivity, Observer { msg ->
                CommonDialog(this@MemoDetailActivity)
                    .setContents(msg)
                    .setPositiveButton(R.string.str_confirm)
                    .show()
            })

            startCopyText.observe(this@MemoDetailActivity, Observer { text ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(getString(R.string.app_name), text)
                JLogger.d("Copy \t$text")
                clipboard.setPrimaryClip(clip)
                showToast(R.string.str_clipboard_copy)
            })

            startMoreDialog.observe(this@MemoDetailActivity, Observer {
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
                                CommonDialog(this@MemoDetailActivity)
                                    .setContents(R.string.memo_delete_guide)
                                    .setNegativeButton(R.string.str_cancel)
                                    .setPositiveButton(R.string.str_confirm)
                                    .setListener(object : CommonDialog.Listener {
                                        override fun onClick(which: Int) {
                                            if (which == CommonDialog.POSITIVE) {
                                                doDeleteMemo()
                                            }
                                        }
                                    }).show()
                            }
                        }
                        moreDialog.dismiss()
                    }
                }.also {
                    it.show(supportFragmentManager, "moreDialog")
                }
            })

            // 갤러리 페이지 진입.
            startGallery.observe(this@MemoDetailActivity, Observer {
                with(RxPermissions(this@MemoDetailActivity)) {
                    request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe { isGranted ->
                        // 동의 한경우.
                        if (isGranted) {
                            startActResult<GalleryActivity>(RequestCode.GALLERY) {
                                putExtra(
                                    ExtraCode.GALLERY_IMG_LIMIT,
                                    0.coerceAtLeast(Etc.IMG_FILE_LIMIT - fileSize.value!!)
                                )
                            }
                        } else {
                            // 권한 확인 안내 팝업 노출
                            CommonDialog(this@MemoDetailActivity)
                                .setContents(R.string.str_permission_denied)
                                .setPositiveButton(R.string.str_confirm)
                                .show()
                        }
                    }
                }
            })

            startFinish.observe(this@MemoDetailActivity, Observer {
                onBackPressed()
            })
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun finish() {
        with(viewModel) {
            when {
                isDelete -> {
                    // 메모 삭제한경우.
                    val intent = Intent()
                    intent.putExtra(ExtraCode.MEMO_DETAIL_POS, memoPosition)
                    intent.putExtra(ExtraCode.MEMO_DETAIL_DELETE, true)
                    intent.putExtra(ExtraCode.MEMO_DETAIL_MANAGE_NO,manageNo.value)
                    setResult(RESULT_OK, intent)
                }
                isMemoChanged() -> {
                    // 메모 데이터가 변경된 경우.
                    val intent = Intent()
                    val bundle = Bundle()
                    bundle.putInt(ExtraCode.MEMO_DETAIL_POS, memoPosition)
                    bundle.putSerializable(
                        ExtraCode.MEMO_DETAIL, MemoItem(
                            manageNo = manageNo.value,
                            tag = selectTag.value,
                            title = title.value!!,
                            contents = contents.value!!,
                            fileList = fileList.value
                        )
                    )
                    intent.putExtras(bundle)
                    setResult(RESULT_OK, intent)
                }
                else -> {
                    setResult(RESULT_CANCELED)
                }
            }

            super.finish()
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