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

    private val _isAuto = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isAuto: MutableLiveData<Boolean>
        get() = _isAuto

    val data = MutableLiveData<LoginResponse>()

    val strId = MutableLiveData<String>()
    val strPw = MutableLiveData<String>()

    val startFinish = SingleLiveEvent<Boolean>()

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
                .doOnSubscribe { JLogger.d("doOnSubscribe") }
                .subscribe({
                    it.loginKey?.let { loginKey ->
//                        actPref.setLoginKey(loginKey)
                        JLogger.d("TEST:: 로그인 성공")
                        startFinish.value = true
                    }
                }, {
                    startFinish.value = false
                })

        }
    }
}