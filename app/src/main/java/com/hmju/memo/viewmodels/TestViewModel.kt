package com.hmju.memo.viewmodels

import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.netIo
import com.hmju.memo.model.test.TestUiModel
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.NetworkDataSource
import com.hmju.memo.utils.JLogger

/**
 * Description : Retrofit And Sealed Class 구현을 위한 테스트용 페이지.
 *
 * Created by juhongmin on 2020/11/24
 */
class TestViewModel(
    private val networkDataSource: NetworkDataSource,
    private val apiService: ApiService
) : BaseViewModel() {

    private val _dataList = ListMutableLiveData<TestUiModel>()
    val dataList: ListMutableLiveData<TestUiModel>
        get() = _dataList

    fun start() {
        launch {
            networkDataSource.fetchMainTest()
                .netIo()
                .doOnSubscribe {
                    onLoading()
                    JLogger.d("Start Thread ${Thread.currentThread()}")
                }
                .subscribe({
                    JLogger.d("Success Thread ${Thread.currentThread()}")
                    _dataList.postValue(it)
                }, {
                    JLogger.d("Error Thread ${Thread.currentThread()}")
                    JLogger.d("Error ${it.message}")
                })
        }
    }
}