package com.hmju.memo.viewModels

import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.single
import com.hmju.memo.extensions.SingleLiveEvent
import com.hmju.memo.model.form.LoginForm
import com.hmju.memo.model.login.LoginResponse
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val apiService: ApiService
) : BaseViewModel() {

    val randomStr = arrayListOf<String>("가", "나", "다", "라", "마", "바")

    val startLogin = SingleLiveEvent<Unit>()

    val startText = MutableLiveData<String>().apply {
        value = ""
    }

    fun moveLogin() {
        startLogin.call()
    }

    fun test() {

//        launch {
//            Observable.just("가", "나", "다", "라")
//                .doOnNext { JLogger.d("onOnNext : $it") }
//                .subscribeOn(Schedulers.newThread())
////                .observeOn(Schedulers.newThread())
//                .subscribe {
//                    JLogger.d("subscribe $it")
//                    startText.postValue(startText.value + it)
//                }
//        }

        launch {
            apiService.fetchMemoList(1)
                .single()
                .subscribe({ response->
                    JLogger.d("onSuccess\t$response")
                },{
                    JLogger.d("onError\t${it.message}")
                },{
                    JLogger.d("onComplete")
                })

        }

    }

    fun foo(): Single<String> {
        val randomThread = arrayOf(100, 200, 300)
        val sleep = randomThread[Random.nextInt(randomThread.size)].toLong()
        JLogger.d("Sleep $sleep")
//        Thread.sleep(sleep)
        return Single.just(randomStr[Random.nextInt(randomStr.size)])
    }

    fun echoTest(): Single<ArrayList<String>> {
        val dummyList = arrayListOf<String>()
        for (i in 0..99) {
            Thread.sleep(100)
            dummyList.add(randomStr[Random.nextInt(randomStr.size)])
            startText.value = dummyList[i]
        }
        return Single.just(dummyList)
    }
}