package com.hmju.memo.viewmodels

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.*
import com.hmju.memo.define.Etc
import com.hmju.memo.define.TagType
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.DataSource
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider

/**
 * Description : 메모 상세 페이지 ViewModel Class
 *
 * Created by juhongmin on 2020/10/25
 */
class MemoDetailViewModel(
        private var originData: MemoItem? = null,
        private val apiService: ApiService,
        private val provider: ImageFileProvider,
        private val resProvider: ResourceProvider,
        private val dataSource: DataSource
) : BaseViewModel() {

    private val _manageNo = NonNullMutableLiveData(originData?.manageNo ?: -1)
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
    val regDtm: String? = originData?.regDtm

    private val isChanged: Boolean
        get() {
            if (title.value.isEmpty() && contents.value.isEmpty()) return false

            return title.value != originData?.title ||
                    contents.value != originData?.contents ||
                    selectTag.value != originData?.tag
        }

    val commitText = NonNullMutableLiveData("") // 수정 및 추가 버튼에 대한 텍스트.

    val startCopyText = SingleLiveEvent<String>()
    val startMoreDialog = SingleLiveEvent<Unit>()
    val startGallery = SingleLiveEvent<Unit>()
    val startSelectedTagColor = SingleLiveEvent<Int>()
    val startImageDetail = SingleLiveEvent<Int>()
    val startFinish = SingleLiveEvent<Boolean>()
    val startKeyboardHide = SingleLiveEvent<Unit>()

    var isDelete = false

    fun initSelectedTag() {
        var tagType = TagType.ETC

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
        return (title.value != originData?.title) ||
                (contents.value != originData?.contents) ||
                (selectTag.value != originData?.tag) ||
                (fileList.value != originData?.fileList)
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
        startSelectedTagColor.value = resProvider.getColor(tag.color)
    }

    fun moveGallery() {
        // 이미지 파일 개수 제한.
        if (Etc.IMG_FILE_LIMIT - fileSize.value!! == 0) {
            startDialog.value = resProvider.getString(R.string.str_guid_limit_img_file_over)
        } else {
            startGallery.call()
        }
    }

    fun postMemo() {
        if (isChanged) {
            JLogger.d("변경된게 있습니다. ")
            postMemo { isSuccess ->
                if (isSuccess) {
                    onSuccess()
                } else {
                    onError()
                }
            }
        } else {
            JLogger.d("변경점이 없습니다.!")
        }
    }

    /**
     * Memo Add and Update
     * @param callBack Api Success CallBack.
     */
    private fun postMemo(callBack: (Boolean) -> Unit) {
//        launch {
//            if (manageNo.value == -1) apiService.postMemo(
//                    MemoItemForm(
//                            tag = selectTag.value,
//                            title = title.value,
//                            contents = contents.value
//                    )
//            ) else {
//                apiService.updateMemo(
//                        MemoItemForm(
//                                manageNo = manageNo.value,
//                                tag = selectTag.value,
//                                title = title.value,
//                                contents = contents.value
//                        )
//                )
//            }.netIo()
//                    .doOnSubscribe { onLoading() }
//                    .subscribe({
//                        JLogger.d("PostMemo Success $it")
//                        saveData()
//
//                        // 메모를 새로 추가 하는 경우.
//                        if (manageNo.value == -1 && it.manageNo != 0) {
//                            JLogger.d("Add Memo")
//                            _manageNo.value = it.manageNo
//                        } else {
//                            // Update 경우
//                            JLogger.d("Update Memo")
//                        }
//                        callBack.invoke(true)
//                    }, {
//                        JLogger.d("PostMemo Error ${it.message}")
//                        callBack.invoke(false)
//                    })
//        }

        launch {
            dataSource.postMemo(
                    MemoItemForm(
                            manageNo = if (manageNo.value == -1) null else manageNo.value,
                            tag = selectTag.value,
                            title = title.value,
                            contents = contents.value
                    )
            )
                    .withIo()
                    .doOnSubscribe { onLoading() }
                    .subscribe({
                        saveData()
                        if (manageNo.value == -1 && it.manageNo != 0) {
                            _manageNo.value = it.manageNo
                        }

                        JLogger.d("onSuccess $it")
                        callBack.invoke(true)
                    }, {
                        JLogger.d("Error ${it.message}")
                        callBack.invoke(false)
                    })
        }
    }

    /**
     * 메모 추가/ 수정 API 호출시 작성한 데이터 저장.
     */
    private fun saveData() {
        if (originData == null) {
            originData = MemoItem(
                    manageNo = manageNo.value,
                    tag = selectTag.value,
                    title = title.value,
                    contents = contents.value
            )
        } else {
            originData?.tag = selectTag.value
            originData?.title = title.value
            originData?.contents = contents.value
        }
    }

    /**
     * 파일 업로드 함수.
     * @param pathList File Path List
     */
    fun addFileUpload(pathList: List<String>) {
        // 메모를 새로 추가하는 경우. 메모 먼저 추가 후 이미지 업로드
        if (manageNo.value == -1) {
            postMemo { isSuccess ->
                if (isSuccess) {
                    addFileUpload(pathList)
                }
            }
            return
        }

        launch {
            dataSource.postMemoImages(
                    manageNo = manageNo.value,
                    pathList = pathList
            )
                    .doOnSubscribe { onLoading() }
                    .subscribe({ response ->
                        JLogger.d("Success Thread ${Thread.currentThread()}")
//                        addImageFileList(response.pathList)
                        // 업로드된 파일 갱신 처리 함수.
                        response.pathList?.let {
                            _fileList.postAddAll(it)
                        }
                        onSuccess()
                    }, { error ->
                        JLogger.d("Error ${error.message} Thread ${Thread.currentThread()}")
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
            ).withIo()
                    .doOnSubscribe { onLoading() }
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
    private fun deleteAllImages(callBack: () -> Unit) {
        launch {
            dataSource.deleteMemoImages(fileList.value)
                    .withIo()
                    .doOnSubscribe { onLoading() }
                    .subscribe({
                        JLogger.d("Delete All Images Success $it")
                        callBack.invoke()
                    }, {
                        JLogger.d("Delete All Images Error ${it.message}")
                        callBack.invoke()
                    })
        }
    }

    /**
     * Delete Memo Func..
     */
    private fun deleteMemo() {
        //        launch {
//            apiService.deleteMemo(
//                    memoId = manageNo.value
//            ).withIo()
//                    .doOnSubscribe { onLoading() }
//                    .subscribe({
//                        JLogger.d("Delete Memo Success $it")
//                        onSuccess()
//                        isDelete = true
//                        startFinish.value = true
//                    }, {
//                        JLogger.d("Delete Memo Error ${it.message}")
//                        onError()
//                        startFinish.value = true
//                    })
//        }

        launch {
            dataSource.deleteMemo(manageNo = manageNo.value)
                    .withIo()
                    .doOnSubscribe {
                        onLoading()
                    }
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

    /**
     * 이미지 자세히 보기 페이지 이동.
     * @param pos -> 현재 바라보고 있는 이미지 위치값
     */
    fun moveDetailImage(pos: Int) {
        startImageDetail.value = pos
    }
}