package com.hmju.memo.ui.login

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityLoginBinding
import com.hmju.memo.dialog.CommonDialog
import com.hmju.memo.viewmodels.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity<ActivityLoginBinding,LoginViewModel> () {

    override val layoutId = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()
    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel){
            startFinish.observe(this@LoginActivity, Observer {isLogin->
                if(isLogin){
                    setResult(Activity.RESULT_OK)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                }
                finish()
            })

            startErrorDialog.observe(this@LoginActivity, Observer {msg ->
                CommonDialog(this@LoginActivity)
                    .setContents(msg)
                    .setPositiveButton(R.string.str_confirm)
                    .show()
            })
        }
    }
}
