package com.hmju.memo.repository.network.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hmju.memo.define.NetworkState
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoListResponse
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
    private val memoParams: MemoListParam,
) : PageKeyedDataSource<Int, MemoItem>() {

    var networkState = MutableLiveData<NetworkState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MemoItem>
    ) {
        // Login Key 유무 검사.
        if (actPref.getLoginKey().isEmpty()) {
            return
        }
        JLogger.d("loadInitial " + params.requestedLoadSize)
        networkState.postValue(NetworkState.LOADING)
        apiService.fetchMemoList(
            pageNo = memoParams.pageNo
        ).enqueue(object : Callback<MemoListResponse> {

            override fun onResponse(call: Call<MemoListResponse>, listResponse: Response<MemoListResponse>) {
                if (listResponse.isSuccessful) {
                    listResponse.body()?.let {
                        memoParams.pageNo++

                        callback.onResult(it.dataList, null, memoParams.pageNo)

                        if (it.dataList.size > 0) {
                            networkState.postValue(NetworkState.SUCCESS)
                        } else {
                            // 처음 Call 할때 데이터가 없는 경우
                            networkState.postValue(NetworkState.RESULT_EMPTY)
                        }

                    } ?: run {
                        // Body Null
                        networkState.postValue(NetworkState.ERROR)
                    }
                } else {
                    // Server Error
                    networkState.postValue(NetworkState.ERROR)
                }
            }

            override fun onFailure(call: Call<MemoListResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
                networkState.postValue(NetworkState.ERROR)
            }

        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MemoItem>) {
        JLogger.d("loadAfter ")
        networkState.postValue(NetworkState.LOADING)
        apiService.fetchMemoList(
            pageNo = memoParams.pageNo
        ).enqueue(object : Callback<MemoListResponse> {

            override fun onResponse(call: Call<MemoListResponse>, listResponse: Response<MemoListResponse>) {
                if (listResponse.isSuccessful) {
                    listResponse.body()?.let {
                        memoParams.pageNo++

                        callback.onResult(it.dataList, memoParams.pageNo)
                        networkState.postValue(NetworkState.SUCCESS)
                    } ?: run {
                        // Body Null
                        networkState.postValue(NetworkState.ERROR)
                    }
                } else {
                    // Server Error
                    networkState.postValue(NetworkState.ERROR)
                }
            }

            override fun onFailure(call: Call<MemoListResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
                networkState.postValue(NetworkState.ERROR)
            }
        })

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MemoItem>) {
        JLogger.d("LoadBefore");
    }
}