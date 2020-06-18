package com.hmju.memo.repository.network

import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Flowable
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Description: API Service
 *
 * Created by juhongmin on 2020/06/05
 */
interface ApiService {

    /**
     * 메모장 데이터 가져오는 API
     * @Query pageNo    : 1부터 시작
     * @Query filterTag : 필터 -> 테그
     * @Query keyWord   : 검색어
     */
    @GET("/api/memoList")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int
    ): Maybe<MemoResponse>

    @GET("/api/memoList")
    fun fetchMemoList(
        @Query("pageNo") pageNo: Int,
        @Query("filterTag") filterTag: Int
    ): Maybe<MemoResponse>

    @GET("/api/memoList")
    fun retrieveSearch(
        @Query("pageNo") pageNo: Int,
        @Query("keyWord") keyWord: String
    ): Maybe<MemoResponse>

    @GET("/api/memoList")
    fun fetchMultiMemoList(
        @Query("pageNo") pageNo: Int
    ): Flowable<MemoResponse>

}