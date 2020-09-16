package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService

/**
 * Description : 메모 자세히 보기 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailViewModel(
    val originData: MemoItem,
    private val apiService: ApiService
) : BaseViewModel() {

    val changeData = NonNullMutableLiveData(originData)

    val startCopyText = SingleLiveEvent<String>()

    fun onCopyText(@IdRes id: Int) {
        if (id == R.id.etTitle) {
            startCopyText.value = changeData.value.title
        } else if (id == R.id.etContents) {
            startCopyText.value = changeData.value.contents
        }
    }
}
