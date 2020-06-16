package com.hmju.memo.viewModels

import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref

/**
 * Description : 메모 추가 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoAddViewModel(
    private val actPref : AccountPref,
    private val apiService: ApiService
) : BaseViewModel() {


}