package com.hmju.memo.viewModels

import com.hmju.memo.base.BaseAdapter.Companion.ItemStruct
import com.hmju.memo.base.BaseAdapter.Companion.TYPE_MEMO_IMG
import com.hmju.memo.base.BaseAdapter.Companion.TYPE_MEMO_NORMAL
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.model.memo.MemoImgItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoNormaItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.JLogger
import io.reactivex.Observable

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
    val startTest = SingleLiveEvent<Unit>()

    //    val dataList = ListMutableLiveData<MemoItem>()
    val dataList = ListMutableLiveData<ItemStruct<*>>()

    fun start() {
        if (actPref.getLoginKey().isNotEmpty()) {
            launch {
                apiService.fetchMemoList(1)
                    .single()
                    .subscribe({
                        dataConverter(it.dataList)
                    }, {

                    })
            }
        } else {
            startLogin.call()
        }
    }

    fun dataConverter(responseList: ArrayList<MemoItem>) {
        launch {
            Observable.fromIterable(responseList)
                .subscribe(
                    {
                        when (it.tag) {
                            in 1..3 -> {
                                dataList.add(
                                    ItemStruct(
                                        MemoImgItem(
                                            title = "${it.title}_첫번째",
                                            contents = it.contents,
                                            images = it.images,
                                            tag = it.tag,
                                            id = it.manageNo
                                        ), TYPE_MEMO_IMG
                                    )
                                )
                            }
                            4 -> {
                                dataList.add(
                                    ItemStruct(
                                        MemoImgItem(
                                            title = "${it.title}_두번쨰",
                                            contents = it.contents,
                                            images = it.images,
                                            tag = it.tag,
                                            id = it.manageNo
                                        ), TYPE_MEMO_IMG
                                    )
                                )
                            }
                            5 -> {
                                dataList.add(
                                    ItemStruct(
                                        MemoNormaItem(
                                            title = "${it.title}_세번쨰",
                                            contents = it.contents,
                                            tag = it.tag,
                                            id = it.manageNo
                                        ), TYPE_MEMO_NORMAL
                                    )
                                )
                            }
                            6 -> {
                                dataList.add(
                                    ItemStruct(
                                        MemoImgItem(
                                            title = "${it.title}_네번쨰",
                                            contents = it.contents,
                                            images = it.images,
                                            tag = it.tag,
                                            id = it.manageNo
                                        ), TYPE_MEMO_IMG
                                    )
                                )
                            }
                            in 7..8 -> {
                                dataList.add(
                                    ItemStruct(
                                        MemoNormaItem(
                                            title = "${it.title}_다섯번째",
                                            contents = it.contents,
                                            tag = it.tag,
                                            id = it.manageNo
                                        ), TYPE_MEMO_NORMAL
                                    )
                                )
                            }
                        }
                    }, {

                    }
                )
        }
    }

    fun test() {
        JLogger.d("TEST!!!!")
    }


    fun moveLogin() {
        startLogin.call()
    }

    fun moveAlert() {
        startAlert.call()
    }

    fun moveTest(){
        startTest.call()
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

