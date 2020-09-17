package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import org.jetbrains.annotations.NotNull

/**
 * Description : 메모 자세히 보기 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailViewModel(
    val memoPosition: Int,
    private val originData: MemoItem,
    private val apiService: ApiService
) : BaseViewModel() {

    private val _changeData = NonNullMutableLiveData(
        MemoItem(
            manageNo = originData.manageNo,
            tag = originData.tag,
            title = originData.title,
            contents = originData.contents,
            fileList = originData.fileList
        )
    )
    val changeData: NonNullMutableLiveData<MemoItem>
        get() = _changeData

    val startFinish = SingleLiveEvent<Unit>()
    val startCopyText = SingleLiveEvent<String>()
    val startAlbumAndCamera = SingleLiveEvent<Int>()
    val selectedFileList = ListMutableLiveData<String>()

    fun onCopyText(@IdRes id: Int) {
        if (id == R.id.etTitle) {
            startCopyText.value = changeData.value.title
        } else if (id == R.id.etContents) {
            startCopyText.value = changeData.value.contents
        }
    }

    /**
     * 처음 받은 데이터와 다른지 유무 체크 함수.
     * @return true -> 변경 O, false -> 변경 X
     */
    fun isMemoChanged(): Boolean {
        // 제목, 내용, 태그 셋중 하나라도 다르다면 메모 변경함
        return (changeData.value.title != originData.title) ||
                (changeData.value.contents != originData.contents) ||
                (changeData.value.tag != originData.tag)
    }

    /**
     * 메모 업데이트
     */
    fun memoUpdate() {
        if (isMemoChanged()) {
            launch {
                apiService.updateMemo(
                    MemoItemForm(
                        manageNo = changeData.value.manageNo,
                        tag = changeData.value.tag,
                        title = changeData.value.title!!,
                        contents = changeData.value.contents!!
                    )
                ).single()
                    .subscribe({
                        JLogger.d("Result $it")
                        startFinish.call()
                    }, {
                        JLogger.d("Fail ${it.message}")
                    })
            }
        }
    }

    fun moveAlbumAndCamera(manageNo : Int){
        startAlbumAndCamera.value = manageNo
    }

    fun fileUploads(){

    }
}
