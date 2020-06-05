package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import kotlinx.coroutines.Deferred

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
class ApiRemoteDataSource (
    private val apiService: ApiService

) : ApiService {

    override suspend fun signIn(body: LoginForm): Deferred<LoginResponse> {
        return apiService.signIn(body)
    }
}