package com.hmju.memo.repository.network.paging.memolist

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListDataSourceFactory(
    private val apiService: ApiService,
    private val params: MemoListParam
) : DataSource.Factory<Int,MemoItem>() {

    val pagedConfig: PagedList.Config by lazy {
        PagedList.Config.Builder()
            .setInitialLoadSizeHint(10)
            .setPageSize(20)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(true)
            .build()
    }

    val sourceLiveData = MutableLiveData<MemoListPageDataSource>()

    override fun create(): DataSource<Int, MemoItem> {
        val source =
            MemoListPageDataSource(
                apiService = apiService,
                memoParam = params
            )
        sourceLiveData.postValue(source)
        return source
    }
}