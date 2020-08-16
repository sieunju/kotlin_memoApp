package com.hmju.memo.ui.login

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.custombehavior.TranslationBehavior
import com.hmju.memo.R
import com.hmju.memo.BR
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.databinding.ActivityLoginBinding
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.viewModels.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity<ActivityLoginBinding,LoginViewModel> () {

    override val layoutId = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()
    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()
        with(viewModel){
            startFinish.observe(this@LoginActivity, Observer {isLogin->
                if(isLogin){
                    setResult(Activity.RESULT_OK)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                }
                finish()
            })

            startLoginFail.observe(this@LoginActivity, Observer {msg ->
                ConfirmDialog(this@LoginActivity,msg).show()
            })
        }
    }
}
