package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.model.memo.MemoDetailInfo
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref

/**
 * Description : 메모 자세히 보기 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailViewModel(
    private val memoInfo: MemoDetailInfo,
    private val actPref: AccountPref,
    private val apiService: ApiService
) : BaseViewModel() {

    val titleStr = MutableLiveData<String>().apply { value = "" }
    val contentsStr = MutableLiveData<String>().apply { value = "" }
    val contentsLength: LiveData<Int> = map(contentsStr) { it.length }
}
