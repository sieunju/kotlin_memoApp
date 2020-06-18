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

    val startPermission = SingleLiveEvent<Unit>()

    fun movePermission(){
        startPermission.call()
    }
}

