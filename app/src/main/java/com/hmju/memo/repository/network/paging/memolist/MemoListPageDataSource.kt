package com.hmju.memo.repository.network.paging.memolist

import androidx.paging.PageKeyedDataSource
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
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListPageDataSource(
    private val actPref: AccountPref,
    private val apiService: ApiService,
    private val memoParam: MemoListParam
) : PageKeyedDataSource<Int, MemoItem>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MemoItem>
    ) {
        // Login Key 유무 검사.
        if(actPref.getLoginKey().isEmpty()) {
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
                        // 이미지 리스트 바인딩 처리.
                        it.dataList.forEach {item->
                            if(!item.isNormal()) {
                                item.bindingImgList()
                            }
                        }
                        callback.onResult(it.dataList, null, memoParam.pageNo)
                    }
                }
            }

            override fun onFailure(call: Call<MemoResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
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
                        // 이미지 리스트 바인딩 처리.
                        it.dataList.forEach {item->
                            if(!item.isNormal()) {
                                item.bindingImgList()
                            }
                        }
                        callback.onResult(it.dataList,memoParam.pageNo)
                    }
                }
            }

            override fun onFailure(call: Call<MemoResponse>, t: Throwable) {
                JLogger.d("onFailure\t${t.message}")
            }
        })

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MemoItem>) {
        JLogger.d("LoadBefore");
    }
}