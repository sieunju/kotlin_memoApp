package com.hmju.memo.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hmju.memo.R
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.define.Etc
import com.hmju.memo.define.TagType
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.ResourceProvider

/**
 * Description : 메모 추가 ViewModel Class
 *
 * Created by hmju on 2020-06-16
 */
class MemoAddViewModel(
    apiService: ApiService,
    provider: ImageFileProvider,
    private val resProvider: ResourceProvider
) : BaseMemoViewModel(
    apiService = apiService,
    provider = provider,
    resProvider = resProvider
) {

    // 메모 추가를 완료했는지 유무
    val isAddMemo: LiveData<Boolean> = Transformations.map(manageNo) { it != -1 }

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