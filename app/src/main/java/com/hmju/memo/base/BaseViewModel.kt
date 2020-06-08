package com.hmju.memo.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

/**
 * Description: BaseViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
open class BaseViewModel : ViewModel() {

    /**
     * CoroutineScope 내부 Exception 처리 Handler
     */
    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    /**
     * Dispatchers 선언 (Normal Dispatchers + CoroutineExceptionHandler)
     */
    protected val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler
    protected val uiDispatchers = Dispatchers.Main + coroutineExceptionHandler


    /*fun launch(job: () -> CoroutineScope){
        CoroutineScope(Dispatchers.IO).launch {
            job
        }
    }*/


    override fun onCleared() {
        super.onCleared()
    }
}