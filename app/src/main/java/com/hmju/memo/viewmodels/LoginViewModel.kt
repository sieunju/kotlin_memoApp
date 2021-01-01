package com.hmju.memo.viewmodels

import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extension.SingleLiveEvent
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.repository.network.login.LoginManager
import com.hmju.memo.utils.JLogger

/**
 * Description: Login ViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
class LoginViewModel(
    private val loginManager: LoginManager
) : BaseViewModel() {

    val data = MutableLiveData<LoginResponse>()

    val strId = MutableLiveData<String>()
    val strPw = MutableLiveData<String>()

    val startFinish = SingleLiveEvent<Boolean>()
    val startErrorDialog = SingleLiveEvent<String>()

    /**
     * 로그인 시작.
     */
    fun startLogin() {
        JLogger.d("Id ${strId.value} Pw ${strPw.value}")
        if (strId.value.isNullOrEmpty() || strPw.value.isNullOrEmpty()) {
            startErrorDialog.value = "제대로 값을 기입 하십시오."
            return
        }

        launch {
            loginManager.loginIn(
                LoginForm(
                    id = strId.value!!.trim(),
                    pw = strPw.value!!.trim()
                )
            )
                .subscribe({
                    JLogger.d("성공 $it")
                    startFinish.value = true
                }, {
                    JLogger.d("실패 $it")
                    startErrorDialog.value = "로그인 실패"
                })
        }
    }

    /**
     * 비 로그인 시작.
     */
    fun startNonLogin() {
        JLogger.d("Non Login Start")
        startFinish.value = false
    }
}