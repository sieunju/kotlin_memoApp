package com.hmju.memo.repository.network.login

import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Description : 로그인 전용 Network Manager
 *
 * Created by juhongmin on 12/19/20
 */
interface LoginManager {

    data class UserInfo(
        var userName: String? = null,
        var profPath: String? = null
    )

    fun user() : MutableLiveData<UserInfo>
    fun isLogin() : MutableLiveData<Boolean>

    fun loginIn(body: LoginForm? = null) : Single<BaseResponse>

    fun loginCheck() : Single<BaseResponse>

    fun logOut()
}