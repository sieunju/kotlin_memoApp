package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Description: API Service
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
    fun signIn(
        @Body body: LoginForm
    ): Maybe<LoginResponse>

    /**
     * 메모장 데이터 가져오는 API
     * @Query pageNo    : 1부터 시작
     * @Query filterTag : 필터 -> 테그
     * @Query keyWord   : 검색어
     */
    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo : Int
    ): Maybe<MemoResponse>

    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo : Int,
        @Query("filterTag") filterTag : Int
    ): Maybe<MemoResponse>

    @GET("/api/memo")
    fun retrieveSearch(
        @Query("pageNo") pageNo : Int,
        @Query("keyWord") keyWord: String
    ): Maybe<MemoResponse>

    @GET("/api/memo")
    fun fetchMultiMemoList(
        @Query("pageNo") pageNo : Int
    ): Flowable<MemoResponse>


    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int,
        @Query("filterTag") filterTag: Int?,
        @Query("keyWord") keyWord: String?
    ): Call<MemoResponse>

}