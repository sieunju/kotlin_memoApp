package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.R
import com.hmju.memo.base.BaseResponse
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.*
import com.hmju.memo.define.Etc
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.TagType
import com.hmju.memo.model.form.DeleteMemoItem
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    val manageNo = NonNullMutableLiveData(originData.manageNo) // 메모 ID
    val regDtm = NonNullMutableLiveData(originData.regDtm) // 수정한 날짜.

    private val _selectTag = NonNullMutableLiveData(originData.tag)
    val selectTag: NonNullMutableLiveData<Int>
        get() = _selectTag // 선택한 우선 순위
    private val _title = NonNullMutableLiveData(originData.title)
    val title: NonNullMutableLiveData<String?>
        get() = _title // 제목
    private val _contents = NonNullMutableLiveData(originData.contents)
    val contents: NonNullMutableLiveData<String?>
        get() = _contents // 내용
    private val _fileList = ListMutableLiveData<FileItem>().apply {
        originData.fileList?.let {
            addAll(it)
        }
    }
    val fileList: ListMutableLiveData<FileItem>
        get() = _fileList
    val fileSize: LiveData<Int> = Transformations.map(fileList) { it.size }

    val startFinish = SingleLiveEvent<Unit>()
    val startCopyText = SingleLiveEvent<String>()
    val startMoreDialog = SingleLiveEvent<Unit>()
    val startGallery = SingleLiveEvent<Unit>()
    val startSelectedTagColor = SingleLiveEvent<Int>()
    var isDelete = false

    init {
        val tagType: TagType
        when (selectTag.value) {
            TagType.RED.tag -> {
                tagType = TagType.RED
            }
            TagType.ORANGE.tag -> {
                tagType = TagType.ORANGE
            }
            TagType.YELLOW.tag -> {
                tagType = TagType.YELLOW
            }
            TagType.GREEN.tag -> {
                tagType = TagType.GREEN
            }
            TagType.BLUE.tag -> {
                tagType = TagType.BLUE
            }
            TagType.PURPLE.tag -> {
                tagType = TagType.PURPLE
            }
            else -> {
                tagType = TagType.ETC
            }
        }

        setSelectedTag(tagType)
    }

    // 제목 OR 내용 글자 복사 함수.
    fun onCopyText(@IdRes id: Int) {
        if (id == R.id.etTitle) {
            startCopyText.value = title.value
        } else if (id == R.id.etContents) {
            startCopyText.value = contents.value
        }
    }

    /**
     * 처음 받은 데이터와 다른지 유무 체크 함수.
     * @return true -> 변경 O, false -> 변경 X
     */
    fun isMemoChanged(): Boolean {
        // 제목, 내용, 태그 셋중 하나라도 다르다면 메모 변경함
        return (title.value != originData.title) ||
                (contents.value != originData.contents) ||
                (selectTag.value != originData.tag) ||
                (fileList.value != originData.fileList)
    }

    /**
     * 메모 업데이트
     */
    fun memoUpdate() {
        if (isMemoChanged()) {
            launch {
                apiService.updateMemo(
                    MemoItemForm(
                        manageNo = manageNo.value,
                        tag = selectTag.value,
                        title = title.value!!,
                        contents = contents.value!!
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

    /**
     * 메모 삭제 및 파일 삭제 호춯 함수.
     * @link{deleteMemo}
     * @link{deleteMemoImgFile}
     * 위 두 함수 개선할 필요가 있음.
     */
    fun doDeleteMemo() {
        startNetworkState.value = NetworkState.LOADING
        deleteMemoImgFile()
    }

    private fun deleteMemoImgFile() {
        launch {
            apiService.deleteFile(
                manageNoList = fileList.value.map { it.manageNo }.toList(),
                pathList = fileList.value.map { it.filePath }.toList()
            ).single()
                .subscribe({
                    JLogger.d("onSuccess $it")
                    deleteMemo()
                }, {
                    JLogger.d("Error ${it.message}")
                    deleteMemo()
                })
        }
    }

    private fun deleteMemo() {
        launch {
            apiService.deleteMemo(memoId = manageNo.value)
                .single()
                .subscribe({
                    JLogger.d("onSuccess $it")
                    startNetworkState.value = NetworkState.SUCCESS
                    isDelete = true
                    startFinish.call()
                }, {
                    JLogger.d("Error $it")
                    startNetworkState.value = NetworkState.ERROR
                })
        }
    }

    /**
     * 파일 업로드 함수.
     * @param filePathList -> 파일 경로 리스
     */
    fun addFileUpload(filePathList: List<String>) {
        JLogger.d("File Upload 진행 합니다.")
        // 업로드 완료후 템프 파일 삭제하도록 처리.
        val tmpFileList = arrayListOf<File>()

        startNetworkState.value = NetworkState.LOADING

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
