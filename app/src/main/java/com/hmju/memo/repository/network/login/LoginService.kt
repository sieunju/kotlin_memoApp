package com.hmju.memo.repository.network.login

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
interface LoginService {

    fun fetchUser(body: LoginForm?) : LoginResponse
}