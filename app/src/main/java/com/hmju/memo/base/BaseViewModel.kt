package com.hmju.memo.base

import androidx.lifecycle.ViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.define.NetworkState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

/**
 * Description: BaseViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
open class BaseViewModel : ViewModel() {

    val startNetworkState = SingleLiveEvent<NetworkState>()
    val startToast = SingleLiveEvent<String>()
    val startDialog = SingleLiveEvent<String>()

    // 코루틴은 잠시 접어둠.. 롤리팝에서 이슈가 있음.
//    /**
//     * CoroutineScope 내부 Exception 처리 Handler
//     */
//    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        throwable.printStackTrace()
//    }
//
//    /**
//     * Dispatchers 선언 (Normal Dispatchers + CoroutineExceptionHandler)
//     */
//    protected val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler
//    protected val uiDispatchers = Dispatchers.Main + coroutineExceptionHandler
//
//
//    fun launch(job: () -> CoroutineScope){
//        CoroutineScope(Dispatchers.IO).launch {
//            job
//        }
//    }

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun launch(job: () -> Disposable) {
        disposable.add(job())
    }

    protected fun onLoading() {
        startNetworkState.postValue(NetworkState.LOADING)
//        if (startNetworkState.value != NetworkState.LOADING) {
//            startNetworkState.value = NetworkState.LOADING
//        }
    }

    protected fun onSuccess() {
        startNetworkState.postValue(NetworkState.SUCCESS)
//        if (startNetworkState.value != NetworkState.SUCCESS) {
//            startNetworkState.value = NetworkState.SUCCESS
//        }
    }

    protected fun onError() {
        startNetworkState.postValue(NetworkState.ERROR)
//        if (startNetworkState.value != NetworkState.ERROR) {
//            startNetworkState.value = NetworkState.ERROR
//        }
    }

    protected fun onResultEmpty() {
        startNetworkState.postValue(NetworkState.RESULT_EMPTY)
//        if (startNetworkState.value != NetworkState.RESULT_EMPTY) {
//            startNetworkState.value = NetworkState.RESULT_EMPTY
//        }
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}