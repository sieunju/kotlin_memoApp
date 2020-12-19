package com.hmju.memo.repository.network.login

import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.repository.network.ApiService
import io.reactivex.Single

class LoginServiceImpl (apiService: ApiService) : LoginService {

    override fun fetchUser(body: LoginForm?): LoginResponse {
        TODO("Not yet implemented")
    }
}