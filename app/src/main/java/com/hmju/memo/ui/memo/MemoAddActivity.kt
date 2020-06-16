package com.hmju.memo.ui.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoAddBinding
import com.hmju.memo.define.ExtraCode.MEMO_MANAGE_NO
import com.hmju.memo.viewModels.MemoAddViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MemoAddActivity : BaseActivity<ActivityMemoAddBinding, MemoAddViewModel>() {

    override val layoutId = R.layout.activity_memo_add
    override val viewModel: MemoAddViewModel by viewModel {
        parametersOf(intent.getStringExtra(MEMO_MANAGE_NO))
    }
    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {

        }
    }
}