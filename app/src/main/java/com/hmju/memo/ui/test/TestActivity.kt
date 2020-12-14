package com.hmju.memo.ui.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityTestBinding
import com.hmju.memo.viewmodels.TestViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Retrofit And Sealed Class 구현을 위한 테스트용 페이지.
 *
 */
class TestActivity : BaseActivity<ActivityTestBinding,TestViewModel>() {
    override val layoutId = R.layout.activity_test
    override val viewModel: TestViewModel by viewModel()
    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel){

            start()
        }
    }
}