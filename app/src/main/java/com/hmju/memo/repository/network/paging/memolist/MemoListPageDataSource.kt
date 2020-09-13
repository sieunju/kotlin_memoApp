package com.hmju.memo.repository.network.paging.memolist

import androidx.paging.PageKeyedDataSource
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.define.NetworkState
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Description : MemoList DataSource
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListPageDataSource(
    private val actPref: AccountPref,
    private val apiService: ApiService,
    private val networkState: SingleLiveEvent<NetworkState>
) : PageKeyedDataSource<Int, MemoItem>() {

    private val memoParam: MemoListParam by lazy {
        MemoListParam(
            pageNo = 1
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MemoItem>
    ) {
        // Login Key 유무 검사.
        if (actPref.getLoginKey().isEmpty()) {
            return
        }
        JLogger.d("loadInitial " + params.requestedLoadSize)
        apiService.fetchMemoList(
            pageNo = memoParam.pageNo
        ).enqueue(object : Callback<MemoResponse> {

            override fun onResponse(call: Call<MemoResponse>, response: Response<MemoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        memoParam.pageNo++

                        callback.onResult(it.dataList, null, memoParam.pageNo)

                        if (it.dataList.size > 0) {
                            networkState.value = NetworkState.SUCCESS
                        } else {
                            // 처음 Call 할때 데이터가 없는 경우
                            networkState.value = NetworkState.RESULT_EMPTY
                        }

                    } ?: run {
                        // Body Null
                        networkState.value = NetworkState.ERROR
                    }
                } else {
                    // Server Error
                    networkState.value = NetworkState.ERROR
                }
            }

            override fun onFailure(call: Call<MemoResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
                networkState.value = NetworkState.ERROR
            }

        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MemoItem>) {
        JLogger.d("loadAfter ")
        apiService.fetchMemoList(
            pageNo = memoParam.pageNo
        ).enqueue(object : Callback<MemoResponse> {

            override fun onResponse(call: Call<MemoResponse>, response: Response<MemoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        memoParam.pageNo++

                        callback.onResult(it.dataList, memoParam.pageNo)
                        networkState.value = NetworkState.SUCCESS
                    } ?: run {
                        // Body Null
                        networkState.value = NetworkState.ERROR
                    }
                } else {
                    // Server Error
                    networkState.value = NetworkState.ERROR
                }
            }

            override fun onFailure(call: Call<MemoResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
                networkState.value = NetworkState.ERROR
            }
        })

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MemoItem>) {
        JLogger.d("LoadBefore");
    }
}