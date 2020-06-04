package com.hmju.memo.base

import android.app.job.JobInfo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Description: BaseViewModel Class
 *
 * Created by juhongmin on 2020/06/04
 */
open class BaseViewModel : ViewModel() {


    /**
     * RxJava 의 observing 을 위한 부분.
     */
    private val compositeDisposable = CompositeDisposable()

    fun launch(job: Job){

    }

    fun addDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)
    }
    /**
     * Activity Lifecycle 맨 마지막 부분
     */
    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}