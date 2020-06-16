package com.hmju.memo.ui.memo

import android.os.Bundle
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMemoDetailBinding
import com.hmju.memo.define.ExtraCode.MEMO_DETAIL
import com.hmju.memo.viewModels.MemoDetailViewModel
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
        parametersOf(intent.getSerializableExtra(MEMO_DETAIL))
    }

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}