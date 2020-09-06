package com.hmju.memo.ui.memo

import android.os.Bundle
import android.view.View
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseFragment
import com.hmju.memo.databinding.FragmentMemoDetailBinding
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.MemoDetailViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Description : 메모 자세히 보기 페이지
 *
 * Created by juhongmin on 2020/09/06
 */
class MemoDetailFragment(private val memoInfo: MemoItem) :
    BaseFragment<FragmentMemoDetailBinding, MemoDetailViewModel>() {

    companion object {
        const val TAG_DETAIL = "MemoDetailFragment"
        fun newInstance(memoInfo: MemoItem): MemoDetailFragment {
            return MemoDetailFragment(memoInfo)
        }
    }

    override val layoutId = R.layout.fragment_memo_detail
    override val viewModel: MemoDetailViewModel by viewModel {
        parametersOf(memoInfo)
    }

    override val bindingVariable = BR.viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {

        }
    }
}