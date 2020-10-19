package com.hmju.memo.viewModels

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.*
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
    private val provider: ImageFileProvider,
    private val resProvider: ResourceProvider
) : BaseMemoViewModel(
    originData = originData,
    apiService = apiService,
    provider = provider,
    resProvider = resProvider
) {

    val regDtm = NonNullMutableLiveData(originData.regDtm) // 수정한 날짜.

    val startCopyText = SingleLiveEvent<String>()
    val startMoreDialog = SingleLiveEvent<Unit>()
    val startGallery = SingleLiveEvent<Unit>()
    val startSelectedTagColor = SingleLiveEvent<Int>()
    val isChanged = MutableLiveData<Boolean>().apply {
                (title.value != originData.title) ||
                (contents.value != originData.contents) ||
                (selectTag.value != originData.tag) ||
                (fileList.value != originData.fileList)
    }

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
}
