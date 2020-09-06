package com.hmju.memo.ui.memo

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseFragment
import com.hmju.memo.databinding.FragmentMemoBinding
import com.hmju.memo.viewModels.MainViewModel
import kotlinx.android.synthetic.main.fragment_memo.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


/**
 * Description : 메모 Fragment Class
 *
 * Created by juhongmin on 2020/09/05
 */
class MemoFragment : BaseFragment<FragmentMemoBinding,MainViewModel> (){

    override val layoutId = R.layout.fragment_memo

    override val viewModel: MainViewModel by sharedViewModel()
    override val bindingVariable = BR.viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            startMemoTop.observe(this@MemoFragment, Observer {
                rvContents.scrollToPosition(0)
            })
        }

    }
}