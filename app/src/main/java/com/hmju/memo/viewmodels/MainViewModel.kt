package com.hmju.memo.viewmodels

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extension.*
import com.hmju.memo.define.NetworkState
import com.hmju.memo.fcm.FCMProvider
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.NetworkDataSource
import com.hmju.memo.repository.network.login.LoginManager
import com.hmju.memo.repository.network.paging.PagingModel
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val actPref: AccountPref,
    private val networkDataSource: NetworkDataSource,
    private val apiService: ApiService,
    private val fcmProvider: FCMProvider,
    private val loginManager: LoginManager
) : BaseViewModel() {

    val startLogin = SingleLiveEvent<Unit>()
    val startToolBarAction = SingleLiveEvent<Int>()
    val startMemoDetail = SingleLiveEvent<Triple<View, MemoItem, Int>>()
    val finish = SingleLiveEvent<Boolean>()

    private val backButtonSubject: Subject<Long> =
        BehaviorSubject.createDefault(0L).toSerialized()

    fun onBackPressed() {
        backButtonSubject.onNext(System.currentTimeMillis())
    }

    private val pagingModel = MutableLiveData<PagingModel<MemoItem>>()
    val memoList: LiveData<PagedList<MemoItem>> =
        switchMap(pagingModel) { it.pagedList }
    val networkState: LiveData<NetworkState> =
        switchMap(pagingModel) { it.networkState }
    val memoSize: LiveData<Int> =
        switchMap(pagingModel) { it.pagedSize }

    val user: MutableLiveData<LoginManager.UserInfo>
        get() = loginManager.user()
    val isLogin: MutableLiveData<Boolean>
        get() = loginManager.isLogin()

    private val params by lazy {
        MemoListParam(
            pageNo = 1
        )
    }

    init {
        addJob(backButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(AndroidSchedulers.mainThread())
            .buffer(2, 1)
            .map { it[0] to it[1] }
            .subscribeWith(object : SimpleDisposableSubscriber<Pair<Long, Long>>() {
                override fun onNext(t: Pair<Long, Long>) {
                    finish.value = t.second - t.first < 2000
                }
            })
        )
        addJob(
            fcmProvider
                .createToken()
                .netIo()
                .subscribe({ token ->
                    if (!token.isNullOrEmpty()) {
                        JLogger.d("FCM Token $token")
                        actPref.setFcmToken(token)
                    }
                }, {
                    JLogger.e("Error " + it.message)

                })
        )
        addJob(
            loginManager.loginCheck().subscribe()
        )
    }

    /**
     * init Data..
     * 현재는 파라미터만 초기화.
     */
    private fun initData() {
        params.pageNo = 1
        params.keyword = null
        params.selectTag = null
    }

    fun start() {
        // 로그인 상태인경우 메모장 API 콜한다.
        if (isLogin.value == true) {
            pagingModel.postValue(networkDataSource.fetchMemoList(params))
        }
    }

    /**
     * 메모 상세 페이지 진입.
     */
    fun memoDetail(view: View, item: MemoItem, pos: Int) {
        startMemoDetail.value = Triple(view, item, pos)
    }

    fun refresh() {
        initData()
        start()
    }

    fun moveLogin() {
        if (isLogin.value == false) {
            startLogin.call()
        } else {
            JLogger.d("로그인 상태입니다. ${memoList.value?.size}")
        }

    }

    fun testStart() {
        launch {
            Single.merge(
                Flowable.fromArray(
                    apiService.fetchMainTest().io().onErrorReturnItem(JsonObject()),
                    apiService.fetchMainTest()
                )
            ).subscribe({

            }, {

            })
        }
    }

    var loading = ObservableField<Boolean>()
    var showNoDataAvailable = ObservableField<Boolean>(false)
    var elements = ObservableField<List<MemoItem>>(emptyList())

    val disposable_: CompositeDisposable = CompositeDisposable()
    var notifyError: (Throwable) -> Unit = {}
    var isCleared = ObservableField<Boolean>(false)

    fun test() {
        disposable_.add(
            apiService.fetchItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loading.set(true)
                }
                .subscribe({ list ->
                    // 성공.
                    if (list.isEmpty()) {
                        showNoDataAvailable.set(true)
                    } else {
                        elements.set(list)
                    }
                    loading.set(false)
                }, {
                    // 에러 리턴.
                    if(isCleared.get() == false) {
                        notifyError.invoke(it)
                        showNoDataAvailable.set(true)
                        loading.set(false)
                    }
                })
        )
    }

    fun error(error: Throwable?) {

    }

    override fun onCleared() {
        super.onCleared()
        isCleared.set(true)
    }
}

