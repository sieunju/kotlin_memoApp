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
import com.hmju.memo.repository.preferences.AccountPref
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

    fun addMemo() {
        launch {
            apiService.addMemo(
                MemoItemForm(
                    tag = selectTag.value,
                    title = title.value,
                    contents = contents.value
                )
            ).single()
                .doOnSubscribe { startNetworkState.value = NetworkState.LOADING }
                .subscribe({
                    _manageNo.value = it.manageNo
                    startNetworkState.value = NetworkState.SUCCESS
                }, {
                    startNetworkState.value = NetworkState.ERROR
                })
        }
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

    private fun addTempMemo(filePathList: List<String>) {
        launch {
            apiService.addMemo(
                MemoItemForm(
                    tag = selectTag.value,
                    title = title.value,
                    contents = contents.value
                )
            ).single()
                .subscribe({
                    JLogger.d("TEST Response $it")
                    _manageNo.value = it.manageNo
                    addFileUpload(filePathList)
                }, {
                    JLogger.d("TEST:: Error ${it.message}")
                    startNetworkState.value = NetworkState.ERROR
                })
        }
    }

    /**
     * 파일 업로드 함수.
     * @param filePathList -> 파일 경로 리스
     */
    fun addFileUpload(filePathList: List<String>) {
        startNetworkState.value = NetworkState.LOADING
        if (manageNo.value == -1) {
            addTempMemo(filePathList)
            return
        }

        JLogger.d("File Upload 진행 합니다.")
        // 업로드 완료후 템프 파일 삭제하도록 처리.
        val tmpFileList = arrayListOf<File>()


        launch {
            // File Path -> File Converter
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
                        memoId = manageNo.value,
                        files = list
                    ).single()
                        .subscribe({
                            // 업로드 완료된 파일들 추가 및 갠신 처리.
                            addImageFileList(it.pathList)

                            provider.deleteFiles(tmpFileList)
                            startNetworkState.value = NetworkState.SUCCESS
                        }, {
                            provider.deleteFiles(tmpFileList)
                            startNetworkState.value = NetworkState.ERROR
                        })
                }, {
                    provider.deleteFiles(tmpFileList)
                    startNetworkState.value = NetworkState.ERROR
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
}