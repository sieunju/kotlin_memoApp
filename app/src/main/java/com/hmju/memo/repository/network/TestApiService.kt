package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Description :
 *
 * Created by juhongmin on 12/13/20
 */
interface TestApiService {

    @GET("/api/mainTest")
    fun fetchMainTest() : Single<JsonObject>


}