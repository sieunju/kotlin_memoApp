package com.hmju.memo.repository.network.paging.memolist

import androidx.arch.core.util.Function
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListDataSourceFactory(
    private val apiService: ApiService,
    private val params: MemoListParam
) : DataSource.Factory<Int,MemoItem>() {

    val sourceLiveData = MutableLiveData<MemoListPageDataSource>()

    override fun create(): DataSource<Int, MemoItem> {
        JLogger.d("onCreate!!")
        val source =
            MemoListPageDataSource(
                apiService = apiService,
                memoParam = params
            )
        sourceLiveData.postValue(source)
        return source
    }

    override fun <ToValue : Any?> map(function: Function<MemoItem, ToValue>): DataSource.Factory<Int, ToValue> {
        JLogger.d("Map ${function}")
        return super.map(function)
    }
}