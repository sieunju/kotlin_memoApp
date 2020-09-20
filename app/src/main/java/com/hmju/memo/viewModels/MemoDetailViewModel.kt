package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.define.NetworkState
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.NotNull
import java.io.File

/**
 * Description : 메모 자세히 보기 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoDetailViewModel(
    val memoPosition: Int,
    private val originData: MemoItem,
    private val apiService: ApiService,
    private val provider: ResourceProvider
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

    fun moveAlbumAndCamera(manageNo: Int) {
        startAlbumAndCamera.value = manageNo
    }

    fun addFileUpload(filePathList: List<String>) {
        JLogger.d("File Upload 진행 합니다.")
        val tmpFileList = arrayListOf<File>()

        startNetworkState.value = NetworkState.LOADING

        launch {
            Observable.fromIterable(filePathList).flatMap { path ->
                Observable.just(provider.getImageFileContents(path))
            }.flatMap { fileInfo ->
                tmpFileList.add(fileInfo.second)
                val body = fileInfo.second.asRequestBody(fileInfo.first)
                Observable.just(
                    MultipartBody.Part.createFormData(
                        name = "files",
                        filename = fileInfo.second.name,
                        body = body
                    )
                )
            }.single()
                .toList()
                .subscribe({ list ->
                    apiService.addFile(
                        memoId = changeData.value.manageNo,
                        files = list
                    ).single()
                        .doOnError { startNetworkState.value = NetworkState.ERROR }
                        .doOnSubscribe { startNetworkState.value = NetworkState.LOADING }
                        .doOnComplete { startNetworkState.value = NetworkState.SUCCESS }
                        .subscribe({
                            JLogger.d("Success $it")
                            val manageNo = changeData.value.manageNo
                            it.pathList?.let{responseFileList->
                                responseFileList.zip(listOf(manageNo)).forEach {  }
                            }
                            provider.deleteFiles(tmpFileList)
                        }, {
                            JLogger.d("Server File ${it.message}")
                            provider.deleteFiles(tmpFileList)
                        })
                }, {
                    JLogger.d("Error\t ${it.message}")
                    provider.deleteFiles(tmpFileList)
                })
        }
    }
}
