package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
interface ApiService {

    /**
     * 로그인 API
     * body {
     *  "user_id" : 아이디,
     *  "user_pw" : 비번
     * }
     */
    @POST("/api/signin")
    suspend fun signIn(
        @Body body: LoginForm
    ): LoginResponse

}