package com.hmju.memo.viewModels

import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Description: Login ViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
class LoginViewModel(
    private val apiService: ApiService,
    private val actPref: AccountPref
) : BaseViewModel() {

    val data = MutableLiveData<LoginResponse>()

    val strId = MutableLiveData<String>()
    val strPw = MutableLiveData<String>()

    val startFinish = SingleLiveEvent<Boolean>()
    val startLoginFail = SingleLiveEvent<String>()

    /**
     * 로그인 시작.
     */
    fun startLogin() {
        JLogger.d("Id ${strId.value} Pw ${strPw.value}")
        launch {
            apiService.signIn(
                LoginForm(
                    id = strId.value!!.trim(),
                    pw = strPw.value!!.trim()
                )
            )
                .single()
                .subscribe({
                    JLogger.d("Result$it")
                    it.loginKey?.let { loginKey ->
                        actPref.setLoginKey(loginKey)
                        JLogger.d("TEST:: 로그인 성공")
                        startFinish.value = true
                    }
                }, {
                    JLogger.d("로그인에 실패했습니다.")
                    startLoginFail.value = "로그인 실패!"
                })
        }
    }

    /**
     * 비 로그인 시작.
     */
    fun startNonLogin(){
        JLogger.d("Non Login Start")
        startFinish.value = false
    }
}