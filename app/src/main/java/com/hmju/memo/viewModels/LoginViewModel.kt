package com.hmju.memo.viewModels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extensions.SingleLiveEvent
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * Description: Login ViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
class LoginViewModel(
    private val apiService: ApiService,
    private val actPref : AccountPref
) : BaseViewModel () {

    private val _isAuto = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isAuto: MutableLiveData<Boolean>
    get() = _isAuto

    val data = MutableLiveData<LoginResponse>()

    val strId = MutableLiveData<String>()
    val strPw = MutableLiveData<String>()

    fun start (){

    }

    fun startLogin(){
        JLogger.d("Id ${strId.value} Pw ${strPw.value}")
        viewModelScope.launch(ioDispatchers) {
            val response = apiService.signIn(
                LoginForm(
                    id = strId.value,
                    pw = strPw.value
                )
            )
            withContext(ioDispatchers){
                JLogger.d("Response $response")
                response.loginKey?.let {loginKey ->
                    actPref.setLoginKey(loginKey)
                }
            }
        }
    }
}