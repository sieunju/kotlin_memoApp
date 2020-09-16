package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.model.memo.MemoDetailInfo
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.preferences.AccountPref

/**
 * Description : 메모 자세히 보기 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailViewModel(
    val originData: MemoItem,
    private val apiService: ApiService
) : BaseViewModel() {

    val changeData = MutableLiveData<MemoItem>().apply { originData }

    val memoTitle: LiveData<String> = map(changeData) { it.title }
    val memoContent: LiveData<String> = map(changeData) { it.contents }

    val startCopyText = SingleLiveEvent<String>()

    fun onCopyText(@IdRes id: Int) {
        if (id == R.id.etTitle) {
            startCopyText.value = memoTitle.value
        } else if (id == R.id.etContents) {
            startCopyText.value = memoContent.value
        }
    }
}
