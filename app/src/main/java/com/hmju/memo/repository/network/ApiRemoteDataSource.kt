package com.hmju.memo.repository.network

import com.google.gson.JsonObject
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.Single
import kotlinx.coroutines.Deferred

/**
 * Description:
 *
 * Created by juhongmin on 2020/06/05
 */
class ApiRemoteDataSource (
    private val apiService: ApiService,
    private val actPref: AccountPref
) : ApiService {

    override fun signIn(body: LoginForm): Single<LoginResponse> {
        return apiService.signIn(body)
    }

    override fun fetchMemoList(pageNo: Int): Single<MemoResponse> {
        return apiService.fetchMemoList(pageNo)
    }
}