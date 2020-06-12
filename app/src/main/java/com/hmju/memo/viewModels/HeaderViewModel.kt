package com.hmju.memo.viewModels

import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref

/**
 * Description : HeaderView 관련 ViewModel
 *
 * Created by hmju on 2020-06-12
 */
class HeaderViewModel(
    private val actPref: AccountPref,
    private val apiService: ApiService
) : BaseViewModel(){

}