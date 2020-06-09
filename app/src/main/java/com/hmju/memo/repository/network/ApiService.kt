package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
//    @POST("/api/signin")
//    fun signIn(
//        @Body body: LoginForm
//    ): Single<LoginResponse>

    @POST("/api/signin")
    fun signIn(
        @Body body: LoginForm
    ): Maybe<LoginResponse>

    /**
     * 메모장 데이터 가져오는 API
     * @Query pageNo : 1부터 시작
     */
//    @GET("/api/memoList")
//    fun fetchMemoList(
//        @Query("pageNo") pageNo : Int
//    ): Single<MemoResponse>

    @GET("/api/memoList")
    fun fetchMemoList(
        @Query("pageNo") pageNo : Int
    ): Maybe<MemoResponse>

}