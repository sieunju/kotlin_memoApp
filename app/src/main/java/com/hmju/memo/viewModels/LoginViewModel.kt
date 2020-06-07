package com.hmju.memo.viewModels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extensions.SingleLiveEvent
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Description: Login ViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
class LoginViewModel(
    private val apiService: ApiService
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
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.signIn(
                LoginForm(
                    id = strId.value,
                    pw = strPw.value
                )
            )
            withContext(Dispatchers.IO){
                try{
                    JLogger.d("Response $response")
                }catch (e : HttpException){
                    JLogger.e("HttpException ${e.message()}")
                } catch (e: Throwable){
                    JLogger.e("Throwable ${e.message}")
                }
            }
        }
    }
}