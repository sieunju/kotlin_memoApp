package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoListResponse
import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import org.json.JSONObject
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
    ): Single<LoginResponse>

    /**
     * 메모장 데이터 가져오는 API
     * @Query pageNo    : 1부터 시작
     * @Query filterTag : 필터 -> 테그
     * @Query keyWord   : 검색어
     */
    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int
    ): Call<MemoListResponse>

    @GET("/api/memo")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int,
        @Query("filterTag") filterTag: Int
    ): Call<MemoListResponse>

    @GET("/api/memo")
    fun retrieveSearch(
        @Query("pageNo") pageNo: Int,
        @Query("keyWord") keyWord: String
    ): Call<MemoListResponse>

    /**
     * 메모 추가 API
     * body {
     *  tag         : Memo Tag,
     *  title       : Memo Title
     *  contents    : Memo Contents
     * }
     */
    @POST("/api/memo")
    fun postMemo(
        @Body body: MemoItemForm
    ): Single<MemoResponse>

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
    ): Single<MemoResponse>

    /**
     * 메모장 삭제 API
     * @query memo_id -> 메모장 관리자 번호
     */
    @DELETE("/api/memo")
    fun deleteMemo(
        @Query("memo_id") memoId: Int
    ): Single<BaseResponse>

    /**
     * 메모장 파일 추가 API
     * Multipart {
     *  memoId  : Memo Uid,
     *  files   : File
     * }
     */
    @Multipart
    @POST("/api/uploads")
    fun uploadFile(
        @Part("memoId") memoId: Int,
        @Part files: ArrayList<MultipartBody.Part>
    ): Single<MemoFileResponse>

    /**
     * 메모장 파일 제거 API
     * @param manageNo 파일 관리자 번호
     * @param path 파일 경로
     */
    @DELETE("/api/uploads")
    fun deleteFile(
        @Query("manageNoList") manageNo: Int,
        @Query("pathList") path: String
    ): Single<BaseResponse>

    /**
     * 메모장 파일 제거 API
     * @param manageNoList 파일 관리자 번호 리스트
     * @param pathList  파일 경로 리스트
     */
    @DELETE("/api/uploads")
    fun deleteFiles(
        @Query("manageNoList") manageNoList: List<Int>,
        @Query("pathList") pathList: List<String>
    ): Single<BaseResponse>

    @GET("/api/mainTest")
    fun fetchMainTest() : Single<JsonObject>
}