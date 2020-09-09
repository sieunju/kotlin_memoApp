package com.hmju.memo.ui.memo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoDetailBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MemoDetailViewModel
import kotlinx.android.synthetic.main.activity_memo_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 메인 자세히 보기 페이지
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailActivity : BaseActivity<ActivityMemoDetailBinding, MemoDetailViewModel>() {

    override val layoutId = R.layout.activity_memo_detail
    override val viewModel: MemoDetailViewModel by viewModel{
        parametersOf(intent.getSerializableExtra(ExtraCode.MEMO_DETAIL))
    }

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {

            startCopyText.observe(this@MemoDetailActivity, Observer { text->
                val clipboard  = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip : ClipData = ClipData.newPlainText("MEMO",text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@MemoDetailActivity,"복사를 완료하였습니다.",Toast.LENGTH_SHORT).show()
            })
        }
    }
}