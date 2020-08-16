package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.android.material.snackbar.Snackbar
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.paging.memolist.MemoListDataSourceFactory
import com.hmju.memo.repository.preferences.AccountPref
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.flowable.FlowableBuffer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.android.synthetic.main.activity_main.*

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
    val finish = SingleLiveEvent<Boolean>()

    private val backButtonSubject : Subject<Long> =
        BehaviorSubject.createDefault(0L).toSerialized()

    fun onBackPressed() {
        backButtonSubject.onNext(System.currentTimeMillis())
    }

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

    private val factory: MemoListDataSourceFactory by lazy {
        MemoListDataSourceFactory(actPref, apiService, memoParam)
    }

    val pagedList: LiveData<PagedList<MemoItem>> =
        LivePagedListBuilder(
            factory, config
        ).build()

    init {
        launch{
            // BackPress 처
            backButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2,1)
                .map { it[0] to it [1] }
                .subscribeWith(object : DisposableSubscriber<Pair<Long, Long>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: Pair<Long, Long>?) {
                        t?.let{
                            finish.value = it.second - it.first < 2000
                        }
                    }

                    override fun onError(t: Throwable?) {
                    }
                })
        }
    }

    fun start() {
        // 로그인 상태인경우 메모장 API 콜한다.
        if (actPref.getLoginKey().isNotEmpty()) {
            _isPageStart.value = true
            factory.refresh()
        } else {
            startLogin.call()
        }

    }
}

