package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
interface ApiService {

    @GET("/user")
    suspend fun getUser(): Deferred<JsonObject>

}