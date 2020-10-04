package com.hmju.memo.repository.network.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger


/**
 * Description : PagedList Data Source Class
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListDataSourceFactory(
    private val apiService: ApiService,
    private val actPref: AccountPref,
    private val memoParams: MemoListParam
) : DataSource.Factory<Int, MemoItem>() {

    val sourceLiveData = MutableLiveData<MemoListPageDataSource>()

    override fun create(): DataSource<Int, MemoItem> {
        val source =
            MemoListPageDataSource(
                actPref = actPref,
                apiService = apiService,
                memoParams = memoParams
            )
        sourceLiveData.postValue(source)
        return source
    }

    /**
     * Factory 갱신 처리 함수.
     */
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