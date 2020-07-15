package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.paging.memolist.MemoListDataSourceFactory
import com.hmju.memo.repository.network.paging.memolist.MemoListPageDataSource
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import java.util.concurrent.Executors

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val actPref: AccountPref,
    private val apiService: ApiService
) : BaseViewModel() {

    private val _isPageStart = MutableLiveData<Boolean>().apply { value = false }
    val isPageStart: MutableLiveData<Boolean>
        get() = _isPageStart

    val startLogin = SingleLiveEvent<Unit>()
    val startAlert = SingleLiveEvent<Unit>()

    val pagedList = MutableLiveData<PagedList<MemoItem>>()

    val memoParam: MemoListParam by lazy {
        MemoListParam(
            pageNo = 1,
            selectedTag = 1,
            keyWord = ""
        )
    }

    val pagedConfig: PagedList.Config by lazy {
        PagedList.Config.Builder()
            .setInitialLoadSizeHint(10)
            .setPageSize(20)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(true)
            .build()
    }

    val executor = Executors.newFixedThreadPool(5)
    var dataFactory: MemoListDataSourceFactory? = null

    fun start() {
        if (actPref.getLoginKey().isNotEmpty()) {
            dataFactory?.let {
                JLogger.d("이미 만들어짐..리셋..")
            } ?: run {
                val builder = RxPagedListBuilder<Int,MemoItem>(object: DataSource.Factory<Int,MemoItem>(){
                    override fun create(): DataSource<Int, MemoItem> {
                        JLogger.d("여기까지 성공..")
                        return MemoListPageDataSource(apiService,memoParam)
                    }
                },pagedConfig)

                builder.buildObservable()
                    .subscribe {
                        pagedList.value = it;
                    }
            }
        } else {
            startLogin.call()
        }

    }
}

