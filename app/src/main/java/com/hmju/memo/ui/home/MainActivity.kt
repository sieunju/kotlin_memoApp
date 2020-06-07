package com.hmju.memo.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.R
import com.hmju.memo.BR

import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.startAct
import com.hmju.memo.viewModels.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding,MainViewModel> (){

    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel){

            startLogin.observe(this@MainActivity, Observer {
                startAct<LoginActivity>()
            })
        }
    }
}
