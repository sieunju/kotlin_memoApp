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
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.random.Random

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel(
    private val actPref: AccountPref,
    private val apiService: ApiService
) : BaseViewModel() {

    val startLogin = SingleLiveEvent<Unit>()
    val startAlert = SingleLiveEvent<Unit>()

    val dataList = ListMutableLiveData<MemoItem>()

    fun start() {
        if (actPref.getLoginKey().isNotEmpty()) {
            launch {
                apiService.fetchMemoList(1)
                    .single()
                    .subscribe({
                        dataList.addAll(it.dataList)
                    }, {

                    })
            }
        } else {
            startLogin.call()
        }
    }

    fun test(){
        JLogger.d("TEST!!!!")
    }


    fun moveLogin() {
        startLogin.call()
    }

    fun moveAlert() {
        startAlert.call()
    }

    // 테스트 코드
    /*val randomStr = arrayListOf<String>("가", "나", "다", "라", "마", "바")*/
    /*val tmpList = ListMutableLiveData<MemoItem>()*/

/*    fun filterTest(){
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
    }*/

    /*fun test() {

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

        launch {
            apiService.fetchMemoList(1)
                .single()
                .map { t: MemoResponse -> t.dataList }
                .subscribe({
                    tmpList.addAll(it)
                }, {
                })
        }
        launch {
            Flowable.mergeDelayError(
                apiService.fetchMultiMemoList(1).multi().flatMap { t -> t.dataList },
                apiService.fetchMultiMemoList(2).multi()
            ).subscribe({
            },{

            },{

            })
        }

        val source = Observable.zip(
            apiService.fetchMultiMemoList(1).multi().map { it.dataList },
            apiService.fetchMultiMemoList(2).multi().map { it.dataList },
            BiFunction<MemoItem, MemoItem, String> { s, n -> "${s.title}\t${n.contents}" })

        launch {
            Flowable.merge(
                listOf(
                    testApiService.firstPage().multi(),
                    testApiService.firstPage().multi().delay(2, TimeUnit.SECONDS),
                    testApiService.firstPage().multi().delay(3, TimeUnit.SECONDS),
                    testApiService.twoPage().multi(),
                    testApiService.twoPage().multi(),
                    testApiService.twoPage().multi()
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    JLogger.d("onSuccess $it")
                }, {

                }, {

                })
        }
    }*/
}

