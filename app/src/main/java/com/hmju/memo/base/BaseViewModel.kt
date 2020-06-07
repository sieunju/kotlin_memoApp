package com.hmju.memo.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

/**
 * Description: BaseViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
open class BaseViewModel : ViewModel() {

    /**
     * CoroutineScope 내부 Exception 처리 Handler
     */
    protected val coroutineExceptionHanlder = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    /**
     * Dispatchers 선언 (Normal Dispatchers + CoroutineExceptionHandler)
     */
    protected val ioDispatchers = Dispatchers.IO + coroutineExceptionHanlder
    protected val uiDispatchers = Dispatchers.Main + coroutineExceptionHanlder


    fun launch(job: () -> CoroutineScope){
        CoroutineScope(Dispatchers.IO).launch {
            job
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}