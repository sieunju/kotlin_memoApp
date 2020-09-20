package com.hmju.memo.ui.memo

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoDetailBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ResultCode
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.ui.album.AlbumActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MemoDetailViewModel
import com.hmju.memo.widget.keyboard.FluidContentResize
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 메인 자세히 보기 페이지
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailActivity : BaseActivity<ActivityMemoDetailBinding, MemoDetailViewModel>() {


    override val layoutId = R.layout.activity_memo_detail
    override val viewModel: MemoDetailViewModel by viewModel {
        parametersOf(
            intent.getIntExtra(ExtraCode.MEMO_DETAIL_POS, -1),
            intent.getSerializableExtra(ExtraCode.MEMO_DETAIL)
        )
    }

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransFormationEndContainer()
        super.onCreate(savedInstanceState)
        // 자연스러운 키보드 올라오기 위한 코드.
        FluidContentResize.listen(this)

        with(viewModel) {

            startNetworkState.observe(this@MemoDetailActivity, Observer {state->
                when(state) {
                    NetworkState.LOADING -> {
                        JLogger.d("로딩중우우우우")
                    }
                    NetworkState.ERROR -> {
                        JLogger.d("에러 입니다아아앙")
                    }
                    else -> {
                        JLogger.d("성공 성공!")
                    }
                }
            })

            startCopyText.observe(this@MemoDetailActivity, Observer { text ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(getString(R.string.app_name), text)
                JLogger.d("Copy \t$text")
                clipboard.setPrimaryClip(clip)
                showToast(R.string.str_clipboard_copy)
            })

            startFinish.observe(this@MemoDetailActivity, Observer {
                onBackPressed()
            })

            startAlbumAndCamera.observe(this@MemoDetailActivity, Observer { manageNo ->
                with(RxPermissions(this@MemoDetailActivity)) {
                    request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ).subscribe { isGranted ->
                        // 동의 한경우.
                        if (isGranted) {
                            startActResult<AlbumActivity>(RequestCode.ALBUM) {
                                putExtra(ExtraCode.ALBUM_MANAGE_NO, manageNo)
                            }
                        } else {
                            // 권한 확인 안내 팝업 노출
                            ConfirmDialog(this@MemoDetailActivity, R.string.str_permission_denied)
                        }
                    }
                }
            })
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun finish() {
        with(viewModel) {
            if (isMemoChanged()) {
                val intent = Intent()
                val bundle = Bundle()
                bundle.putInt(ExtraCode.MEMO_DETAIL_POS, memoPosition)
                bundle.putSerializable(ExtraCode.MEMO_DETAIL, changeData.value)
                intent.putExtras(bundle)
                setResult(RESULT_OK, intent)
            } else {
                setResult(RESULT_CANCELED)
            }

            super.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.ALBUM -> {
                if(resultCode == RESULT_OK) {
                    JLogger.d("TEST:: 데이터를 전달 받았습니다.")
                    with(viewModel){
                        data?.getStringArrayListExtra(ExtraCode.ALBUM_SELECT_IMAGES)?.let{
                            addFileUpload(it)
                        }
                    }
                } else {
                    JLogger.d("아무 동작 안함")
                }

            }
        }
    }

    fun doAlbum() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RequestCode.ALBUM)
    }
}