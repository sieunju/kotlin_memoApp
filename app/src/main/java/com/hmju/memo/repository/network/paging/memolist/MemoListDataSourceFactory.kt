package com.hmju.memo.repository.network.paging.memolist

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListDataSourceFactory(
    private val actPref: AccountPref,
    private val apiService: ApiService,
    private val params: MemoListParam
) : DataSource.Factory<Int, MemoItem>() {

    private val sourceLiveData = MutableLiveData<MemoListPageDataSource>()

    override fun create(): DataSource<Int, MemoItem> {
        JLogger.d("onCreate!!")
        val source =
            MemoListPageDataSource(
                actPref = actPref,
                apiService = apiService,
                memoParam = params
            )
        sourceLiveData.postValue(source)
        return source
    }

    fun refresh() {
        JLogger.d("갱신 갱신!")
        val item = sourceLiveData.value
        item?.let {
            JLogger.d("실제로 갱신 합니다.")
            item.invalidate()
        } ?: run {
            JLogger.d("PageDataSource Null...")
        }
    }
}