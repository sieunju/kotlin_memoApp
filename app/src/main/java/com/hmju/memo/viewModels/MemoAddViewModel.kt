package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.define.Etc
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.TagType
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Description : 메모 추가 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoAddViewModel(
    private val apiService: ApiService,
    private val provider: ResourceProvider
) : BaseViewModel() {

    private val _manageNo = NonNullMutableLiveData(-1)
    val manageNo: NonNullMutableLiveData<Int>
        get() = _manageNo // 메모 관리자 번호
    val isAddMemo: LiveData<Boolean> = Transformations.map(manageNo) { it != -1 }
    // 메모 추가를 완료했는지 유무

    private val _selectTag = NonNullMutableLiveData(TagType.ETC.tag)
    val selectTag: NonNullMutableLiveData<Int>
        get() = _selectTag // 선택한 우선 순위
    private val _title = NonNullMutableLiveData("")
    val title: NonNullMutableLiveData<String>
        get() = _title // 제목
    private val _contents = NonNullMutableLiveData("")
    val contents: NonNullMutableLiveData<String>
        get() = _contents // 내용
    private val _fileList = ListMutableLiveData<FileItem>()
    val fileList: ListMutableLiveData<FileItem>
        get() = _fileList
    val fileSize: LiveData<Int> = Transformations.map(fileList) { it.size }

    val startMoreDialog = SingleLiveEvent<Unit>()
    val startGallery = SingleLiveEvent<Unit>()
    val startSelectedTagColor = SingleLiveEvent<Int>()


    init {
        setSelectedTag(TagType.ETC)
    }

    fun moveMoreDialog() {
        startMoreDialog.call()
    }

    /**
     * 선택한 우선순위 세팅.
     * @param tag -> Selected Tag.
     */
    fun setSelectedTag(tag: TagType) {
        selectTag.value = tag.tag
        startSelectedTagColor.value = provider.getColor(tag.color)
    }

    fun moveGallery() {
        // 이미지 파일 개수 제한.
        if (Etc.IMG_FILE_LIMIT - fileSize.value!! == 0) {
            startDialog.value = provider.getString(R.string.str_guid_limit_img_file_over)
        } else {
            startGallery.call()
        }
    }

    fun addMemo() {
        addMemo { null }
    }

    private fun addMemo(callBack: (Boolean) -> Unit?) {

        if (isAddMemo.value!!) {
            JLogger.d("이미 메모가 추가된 상태입니다.")
            launch {
                apiService.updateMemo(
                    MemoItemForm(
                        manageNo = manageNo.value,
                        tag = selectTag.value,
                        title = title.value,
                        contents = contents.value
                    )
                ).single()
                    .doOnSubscribe { onLoading() }
                    .subscribe(
                        {
                            JLogger.d("Success $it")
                            onSuccess()
                        }, {
                            JLogger.d("Error ${it.message}")
                            onError()
                        })
            }
        } else {
            JLogger.d("아닙니다!")
            launch {
                apiService.addMemo(
                    MemoItemForm(
                        tag = selectTag.value,
                        title = title.value,
                        contents = contents.value
                    )
                ).single()
                    .doOnSubscribe { onLoading() }
                    .subscribe(
                        {
                            JLogger.d("Success $it")
                            if (it.manageNo != -1) {
                                _manageNo.value = it.manageNo
                                onSuccess()
                                callBack.invoke(true)
                            } else {
                                JLogger.d("Error 입니다.")
                                onError()
                                callBack.invoke(false)
                            }
                        }, {
                            JLogger.d("Error ${it.message}")
                            onError()
                            callBack.invoke(false)
                        })
            }
        }
    }

    /**
     * 파일 업로드 함수.
     * @param filePathList -> 파일 경로 리스트
     */
    fun addFileUpload(filePathList: List<String>) {
        if (manageNo.value == -1) {
            addMemo { isSuccess ->
                if (isSuccess) {
                    addFileUpload(filePathList)
                }
            }
            return
        }

        JLogger.d("File Upload 진행 합니다.")
        // 업로드 완료후 템프 파일 삭제하도록 처리.
        val tmpFileList = arrayListOf<File>()

        launch {
            onLoading()
            // File Path -> File Converter
            Observable.fromIterable(filePathList)
                .flatMap { path ->
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
                .subscribe(
                    { list ->
                        apiService.addFile(
                            memoId = manageNo.value,
                            files = list
                        ).single()
                            .subscribe({
                                // 업로드 완료된 파일들 추가 및 갠신 처리.
                                addImageFileList(it.pathList)

                                provider.deleteFiles(tmpFileList)
                                onSuccess()
                            }, {
                                provider.deleteFiles(tmpFileList)
                                onError()
                            })
                    }, {
                        provider.deleteFiles(tmpFileList)
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
     * 파일 삭제 함수.
     * @param fileItem Current File Item
     */
    fun deleteImage(fileItem: FileItem) {
        JLogger.d("Delete File $fileItem")
        launch {
            apiService.deleteFile(
                manageNo = fileItem.manageNo,
                path = fileItem.filePath
            ).single()
                .doOnSubscribe {
                    JLogger.d("Api Call")
                    onLoading()
                }
                .subscribe({
                    JLogger.d("Success $it")
                    _fileList.postRemove(
                        fileItem
                    )
                    onSuccess()
                }, {
                    JLogger.d("Error ${it.message}")
                    onError()
                })
        }
    }
}