package com.hmju.memo.repository.network

import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.hmju.memo.convenience.single
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Call

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
class ApiRemoteDataSource (
    private val apiService: ApiService,
    private val actPref: AccountPref
) : ApiService {

    override fun signIn(body: LoginForm): Maybe<LoginResponse> {
        return apiService.signIn(body)
    }

    override fun fetchMemoList(pageNo: Int): Call<MemoResponse> {


        return apiService.fetchMemoList(pageNo)
    }

    override fun fetchMemoList(pageNo: Int, filterTag: Int): Maybe<MemoResponse> {
        return apiService.fetchMemoList(pageNo,filterTag)
    }

    override fun retrieveSearch(pageNo: Int, keyWord: String): Maybe<MemoResponse> {
        return apiService.retrieveSearch(pageNo, keyWord)
    }
}