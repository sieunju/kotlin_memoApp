package com.hmju.memo.repository.network

import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Maybe
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

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
        @Query("pageNo") pageNo: Int
    ): Call<MemoResponse>

    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int,
        @Query("filterTag") filterTag: Int
    ): Maybe<MemoResponse>

    @GET("/api/memo")
    fun retrieveSearch(
        @Query("pageNo") pageNo: Int,
        @Query("keyWord") keyWord: String
    ): Maybe<MemoResponse>

    /**
     * 메모장 데이터 수정 API
     * body {
     *  memo_id     : Memo Uid,
     *  tag         : Memo Tag,
     *  title       : Memo Title,
     *  contents    : Memo Contents
     * }
     */
    @PUT("/api/memo")
    fun updateMemo(
        @Body body: MemoItemForm
    ): Maybe<BaseResponse>

    /**
     * 메모장 파일 추가 API
     * Multipart {
     *  memoId  : Memo Uid,
     *  files   : File
     * }
     */
    @Multipart
    @POST("/api/uploads")
    fun addFile(
        @Part("memoId") memoId: Int,
        @Part files: List<MultipartBody.Part>
    ): Maybe<MemoFileResponse>

    /**
     * 메모장 파일 제거 API
     * body {
     *  resPath : 지우고 싶은 파일 주소.
     * }
     */
    @DELETE("/api/uploads")
    fun deleteFile(
        @Body body: String
    ): Maybe<BaseResponse>
}