package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.*
import com.hmju.memo.define.TagType
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Flowable
import okhttp3.MultipartBody
import java.io.File

/**
 * Description : 메모 상세, 메모 추가 관련 BaseViewModel Class
 *
 * Created by juhongmin on 2020/10/11
 */
abstract class BaseMemoViewModel(
    private val originData: MemoItem? = null,
    private val apiService: ApiService,
    private val provider: ImageFileProvider,
    private val resProvider: ResourceProvider
) : BaseViewModel() {

    private val _manageNo = NonNullMutableLiveData(-1)
    val manageNo: NonNullMutableLiveData<Int>
        get() = _manageNo
    private val _selectTag = NonNullMutableLiveData(originData?.tag ?: TagType.ETC.tag)
    val selectTag: NonNullMutableLiveData<Int>
        get() = _selectTag // 선택한 우선 순위
    private val _title = NonNullMutableLiveData(originData?.title ?: "")
    val title: NonNullMutableLiveData<String>
        get() = _title // 제목
    private val _contents = NonNullMutableLiveData(originData?.contents ?: "")
    val contents: NonNullMutableLiveData<String>
        get() = _contents // 내용
    private val _fileList = ListMutableLiveData<FileItem>().apply {
        originData?.fileList?.let {
            addAll(it)
        }
    }
    val fileList: ListMutableLiveData<FileItem>
        get() = _fileList
    val fileSize: LiveData<Int> = Transformations.map(fileList) { it.size }

    // true API 호출 하면서 갱신 처리
    // false 내부적으로 Adapter 갱신 처리
    val startFinish = SingleLiveEvent<Boolean>()
    var isDelete = false

    fun postMemo() {
        postMemo { null }
    }

    fun postMemo(callBack: (Boolean) -> Unit?) {

        launch {

            if (manageNo.value == -1) apiService.postMemo(
                MemoItemForm(
                    tag = selectTag.value,
                    title = title.value,
                    contents = contents.value
                )
            ) else {
                apiService.updateMemo(
                    MemoItemForm(
                        manageNo = manageNo.value,
                        tag = selectTag.value,
                        title = title.value,
                        contents = contents.value
                    )
                )
            }.netIo()
                .doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("PostMemo Success $it")

                    // 메모를 추가 하는 경우. callBack 사용
                    if (manageNo.value == -1 && it.manageNo != 0) {
                        _manageNo.value = it.manageNo
                        callBack.invoke(true)
                    } else if (manageNo.value == -1) {
                        callBack.invoke(false)
                    }
                    onSuccess()
                }, {
                    JLogger.e("PostMemo Error ${it.message}")
                    callBack.invoke(false)
                    onError()
                })
        }
    }

    fun doImageUpload(pathList: List<String>) {

    }

    /**
     * 파일 업로드 함수.
     * @param filePathList -> 파일 경로 리스트
     */
    fun addFileUpload(pathList: List<String>) {
        if (manageNo.value == -1) {
            postMemo { isSuccess ->
                if (isSuccess) {
                    addFileUpload(pathList)
                }
            }
            return
        }

        val tmpFileList = arrayListOf<File>()

        launch {
            Flowable.fromIterable(pathList)
                .flatMap { path ->
                    JLogger.d("First Thread ${Thread.currentThread()}")
                    Flowable.just(provider.createMultiPartBody(path)).computeOn()
                }.flatMap { multiPartBody ->
                    JLogger.d("Second Thread ${Thread.currentThread()}")
                    tmpFileList.add(multiPartBody.second)
                    Flowable.just(
                        MultipartBody.Part.createFormData(
                            name = "files",
                            filename = multiPartBody.second.name,
                            body = multiPartBody.first
                        )
                    ).computeOn()
                }.compute()
                .toList()
                .flatMap { list ->
                    JLogger.d("Third Thread ${Thread.currentThread()}")
                    apiService.uploadFile(
                        memoId = manageNo.value,
                        files = list
                    ).io()
                }
                .doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("Response $it")
                    // Response 처리.
                    addImageFileList(it.pathList)

                    tmpFileList.forEach { provider.deleteFile(it) }
                    onSuccess()
                }, {
                    JLogger.d("Error $it")
                    tmpFileList.forEach { provider.deleteFile(it) }
                    onError()
                })
        }
    }

    /**
     * 업로드 완료된 파일리스트
     * 현재 페이지에 갱신 처리 함수.
     * @param list -> 업로드 완료된 파일 경로 리스트
     */
    private fun addImageFileList(tmpList: ArrayList<FileItem>?) {

        tmpList?.let { filePathList ->
            _fileList.postAddAll(filePathList)
        }
    }

    /**
     * 이미지 삭제 함수.
     * @param fileItem Current File Item
     */
    fun deleteImage(fileItem: FileItem) {
        launch {
            apiService.deleteFile(
                manageNo = fileItem.manageNo,
                path = fileItem.filePath
            ).netIo().doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("Delete Image Success $it")
                    _fileList.postRemove(fileItem)
                    onSuccess()
                }, {
                    JLogger.e("Delete Image Error ${it.message}")
                    onError()
                })
        }
    }

    /**
     * Call Memo Delete
     * 해당 메모에 포함된 이미지부터 삭제 이후 메모 삭제.
     */
    fun doDeleteMemo() {
        if (fileSize.value!! > 0) {
            deleteAllImages {
                deleteMemo()
            }
        } else {
            deleteMemo()
        }
    }

    /**
     * Delete All Images
     * @param callBack Api Service
     */
    private fun deleteAllImages(callBack: (Boolean) -> Unit) {
        launch {
            apiService.deleteFiles(
                manageNoList = fileList.value.map { it.manageNo }.toList(),
                pathList = fileList.value.map { it.filePath }.toList()
            ).netIo()
                .doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("Delete All Images Success $it")
                    callBack.invoke(true)
                }, {
                    JLogger.d("Delete All Images Error ${it.message}")
                    callBack.invoke(false)
                })
        }
    }

    /**
     * Delete Memo Func..
     */
    protected fun deleteMemo() {
        launch {
            apiService.deleteMemo(
                memoId = manageNo.value
            ).netIo()
                .doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("Delete Memo Success $it")
                    onSuccess()
                    isDelete = true
                    startFinish.value = true
                }, {
                    JLogger.d("Delete Memo Error ${it.message}")
                    onError()
                    startFinish.value = true
                })
        }
    }

}