package com.hmju.memo.viewModels

import androidx.lifecycle.MutableLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.multi
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.TestApiService
import com.hmju.memo.utils.JLogger
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.random.Random

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val apiService: ApiService,
    private val testApiService: TestApiService
) : BaseViewModel() {

    val randomStr = arrayListOf<String>("가", "나", "다", "라", "마", "바")

    val startLogin = SingleLiveEvent<Unit>()
    val startAlert = SingleLiveEvent<Unit>()

    val startText = MutableLiveData<String>().apply {
        value = ""
    }

    val tmpList = ListMutableLiveData<MemoItem>()

    fun moveLogin() {
        startLogin.call()
    }

    fun moveAlert() {
        startAlert.call()
    }

    fun filterTest(){
        launch {
            Observable.fromIterable(tmpList.value)
                .filter{obj:MemoItem -> obj.tag == 1 }
                .subscribe({
                    JLogger.d("Filter Item\t${it.manageNo}")
                },{
                    JLogger.d("Filter Error\t${it.message}")
                },{
                    JLogger.d("Filter onComplete")
                })
        }
    }

    fun test() {

        launch {
            Flowable.mergeDelayError(
                apiService.fetchMultiMemoList(1).multi().map { it: MemoResponse -> it.dataList },
                apiService.fetchMultiMemoList(2).multi().map { it: MemoResponse -> it.dataList }
            ).subscribe({
                tmpList.addAll(it)
            }, {
                JLogger.d("API Error\t${it.message}")
            }, {
                JLogger.d("API 호출 완료!")
                filterTest()
            })
        }

//        launch {
//            apiService.fetchMemoList(1)
//                .single()
//                .map { t: MemoResponse -> t.dataList }
//                .subscribe({
//                    tmpList.addAll(it)
//                }, {
//                })
//        }
//        launch {
//            Flowable.mergeDelayError(
//                apiService.fetchMultiMemoList(1).multi().flatMap { t -> t.dataList },
//                apiService.fetchMultiMemoList(2).multi()
//            ).subscribe({
//            },{
//
//            },{
//
//            })
//        }

//        val source = Observable.zip(
//            apiService.fetchMultiMemoList(1).multi().map { it.dataList },
//            apiService.fetchMultiMemoList(2).multi().map { it.dataList },
//            BiFunction<MemoItem, MemoItem, String> { s, n -> "${s.title}\t${n.contents}" })

//        launch {
//            Flowable.merge(
//                listOf(
//                    testApiService.firstPage().multi(),
//                    testApiService.firstPage().multi().delay(2, TimeUnit.SECONDS),
//                    testApiService.firstPage().multi().delay(3, TimeUnit.SECONDS),
//                    testApiService.twoPage().multi(),
//                    testApiService.twoPage().multi(),
//                    testApiService.twoPage().multi()
//                )
//            )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    JLogger.d("onSuccess $it")
//                }, {
//
//                }, {
//
//                })
//        }

    }

    data class MergeData(
        val oneData: MemoResponse,
        val twoData: MemoResponse
    )

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

