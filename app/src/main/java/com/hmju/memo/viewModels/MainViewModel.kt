package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.paging.memolist.MemoListDataSourceFactory
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger

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

    private val memoParam: MemoListParam by lazy {
        MemoListParam(
            pageNo = 1
        )
    }

    private val config: PagedList.Config by lazy {
        PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(true)
            .build()
    }

    var pagedList: LiveData<PagedList<MemoItem>>? = null

    fun start() {
        // 로그인 상태인경우 메모장 API 콜한다.
        if (actPref.getLoginKey().isNotEmpty()) {
            JLogger.d("Start Api")

            pagedList?.let {

            } ?: run {
                JLogger.d("Builder Create")
                pagedList = LivePagedListBuilder(
                    MemoListDataSourceFactory(apiService, memoParam), config
                ).build()
            }

            _isPageStart.value = true
        } else {
            startLogin.call()
        }

    }
}

