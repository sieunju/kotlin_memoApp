package com.hmju.memo.viewModels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoItemAndView
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.paging.memolist.MemoListDataSourceFactory
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.DeviceProvider
import com.hmju.memo.utils.JLogger
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import io.reactivex.subscribers.DisposableSubscriber

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val actPref: AccountPref,
    private val deviceProvider: DeviceProvider,
    private val apiService: ApiService
) : BaseViewModel() {

    val startLogin = SingleLiveEvent<Unit>()
    val startMemo = SingleLiveEvent<Unit>()
    val startMemoTop = SingleLiveEvent<Unit>()
    val startAlert = SingleLiveEvent<Unit>()
    val startToolBarAction = SingleLiveEvent<Int>()
    val startMemoDetail = SingleLiveEvent<MemoItemAndView>()
    val finish = SingleLiveEvent<Boolean>()

    private val backButtonSubject: Subject<Long> =
        BehaviorSubject.createDefault(0L).toSerialized()

    fun onBackPressed() {
        backButtonSubject.onNext(System.currentTimeMillis())
    }

    private val config: PagedList.Config by lazy {
        PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(true)
            .build()
    }

    private val factory: MemoListDataSourceFactory by lazy {
        MemoListDataSourceFactory(actPref, apiService, startNetworkState)
    }

    val pagedList: LiveData<PagedList<MemoItem>> =
        LivePagedListBuilder(
            factory, config
        ).build()

    init {
        launch {
            // BackPress 처리
            backButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2, 1)
                .map { it[0] to it[1] }
                .subscribeWith(object : DisposableSubscriber<Pair<Long, Long>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: Pair<Long, Long>?) {
                        t?.let {
                            finish.value = it.second - it.first < 2000
                        }
                    }

                    override fun onError(t: Throwable?) {
                    }
                })
        }

        if (deviceProvider.isNavigationBar()) {
            JLogger.d("네비게이션바 있다.")
        } else {
            JLogger.d("네비게이션바 없다.")
        }

    }

    fun start() {
        // 로그인 상태인경우 메모장 API 콜한다.
        if (actPref.getLoginKey().isNotEmpty()) {
            startMemo.call()
            factory.refresh()
        } else {
            startLogin.call()
        }
    }

    fun memoDetail(view: View, item: MemoItem) {
        startMemoDetail.value = MemoItemAndView(view = view, item = item)
    }
}

