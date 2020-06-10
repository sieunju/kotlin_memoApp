package com.hmju.memo.repository.network

import com.hmju.memo.model.memo.MemoResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Description :
 *
 * Created by hmju on 2020-06-10
 */
interface TestApiService {

    @GET("/api/memoList?pageNo=1")
    fun firstPage() : Flowable<MemoResponse>

    @GET("/api/memoList?pageNo=2")
    fun twoPage() : Flowable<MemoResponse>

}