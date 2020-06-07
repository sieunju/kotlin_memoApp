package com.hmju.memo.viewModels

import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extensions.SingleLiveEvent
import com.hmju.memo.repository.network.ApiService

/**
 * Description: MainViewModel Class
 *
 * Created by juhongmin on 2020/06/07
 */
class MainViewModel (
    private val apiService: ApiService
) : BaseViewModel(){

    val startLogin = SingleLiveEvent<Unit>()

    fun moveLogin(){
        startLogin.call()
    }
}