package com.hmju.memo.repository.network

import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.preferences.AccountPref
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
class ApiRemoteDataSource(
    private val apiService: ApiService,
    private val actPref: AccountPref
) : ApiService {

    override fun fetchMemoList(pageNo: Int): Maybe<MemoResponse> {
        return apiService.fetchMemoList(pageNo)
    }

    override fun fetchMemoList(pageNo: Int, filterTag: Int): Maybe<MemoResponse> {
        return apiService.fetchMemoList(pageNo, filterTag)
    }

    override fun retrieveSearch(pageNo: Int, keyWord: String): Maybe<MemoResponse> {
        return apiService.retrieveSearch(pageNo, keyWord)
    }

    override fun fetchMultiMemoList(pageNo: Int): Flowable<MemoResponse> {
        return apiService.fetchMultiMemoList(pageNo)
    }
}