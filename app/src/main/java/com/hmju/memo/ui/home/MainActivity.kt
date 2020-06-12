package com.hmju.memo.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hmju.memo.R
import com.hmju.memo.BR

import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.RequestCode
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.startAct
import com.hmju.memo.utils.startActResult
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
                JLogger.d("TEST::고고?")
                startActResult<LoginActivity>(RequestCode.LOGIN){}
            })

            startAlert.observe(this@MainActivity, Observer {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setMessage("안녕하세요. 테스트 입니다.")
                    .setNegativeButton("거절",null)
                    .setPositiveButton("확인",null)
                    .show()
            })

            start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RequestCode.LOGIN -> {
                if(resultCode == Activity.RESULT_OK){
                    with(viewModel){
                        start()
                    }
                }
            }
        }
    }
}
