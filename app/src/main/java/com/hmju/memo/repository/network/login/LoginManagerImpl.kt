package com.hmju.memo.repository.network.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.extension.netIo
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveMain
import io.reactivex.Single

class LoginManagerImpl(
    private val context: Context,
    private val apiService: ApiService,
    private val actPref: AccountPref
) : LoginManager {

    private val _user = MutableLiveData<LoginManager.UserInfo>()
    private val _isLogin = MutableLiveData<Boolean>().apply {
        value = actPref.getLoginKey().isNotEmpty()
    }

    override fun user(): MutableLiveData<LoginManager.UserInfo> {
        return _user
    }

    override fun isLogin(): MutableLiveData<Boolean> {
        return _isLogin
    }

    override fun loginIn(body: LoginForm?): Single<BaseResponse> {
        return if (body == null)
            apiService.loginCheck()
        else {
            apiService.loginIn(body)
        }.netIo()
            .doOnError {
                JLogger.d("LoginIn Error $it")
                _user.value = null
                _isLogin.value = false
            }
            .flatMap { response ->

                _user.value?.let {
                    it.userName = response.userName
                    it.profPath = response.resPath
                } ?: run {
                    _user.value = LoginManager.UserInfo(
                        userName = response.userName,
                        profPath = response.resPath
                    )
                }

                response.loginKey?.let {
                    if (it.isNotEmpty()) {
                        actPref.setLoginKey(it)
                        _isLogin.value = true
                    }
                }

                return@flatMap Single.create<BaseResponse> {
                    it.onSuccess(response)
                }
            }
    }

    override fun loginCheck(): Single<BaseResponse> {
        return loginIn()
    }

    override fun logOut() {
        _user.value = null

        actPref.setLoginKey("")
        if (context is AppCompatActivity) {
            context.moveMain()
        }
    }
}