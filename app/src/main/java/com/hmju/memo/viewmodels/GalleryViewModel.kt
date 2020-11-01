package com.hmju.memo.viewmodels

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.*
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.model.gallery.GallerySelectedItem
import com.hmju.memo.utils.CursorProvider
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Flowable

/**
 * Description : 앨범 및 카메라 ViewModel Class
 *
 * Created by hmju on 2020-09-17
 */
class GalleryViewModel(
    private val limitImageSize: Int,
    private val provider: CursorProvider,
    val fileProvider: ImageFileProvider,
    private val resProvider: ResourceProvider
) : BaseViewModel() {

    companion object {
        val tmpGallerySelectItem = GallerySelectedItem(id = "", pos = -1)
    }

    // 갤러리에서 사진이 추가 / 삭제 되는 경우 갱신 처리.
    private val contentObserver: ContentObserver by lazy {
        return@lazy object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                fetchFilter();
            }
        }
    }
    val cursor = MutableLiveData<Cursor>()
    val startCamera = SingleLiveEvent<Unit>()
    val startSubmit = SingleLiveEvent<Unit>()
    val startFilter = SingleLiveEvent<Unit>()
    val startImageEdit = SingleLiveEvent<String>()
    val startNotify = SingleLiveEvent<GallerySelectedItem>()

    private val _filterList = ListMutableLiveData<GalleryFilterItem>() // 필터 영역
    val filterList: ListMutableLiveData<GalleryFilterItem>
        get() = _filterList
    private val _selectedFilter = MutableLiveData<GalleryFilterItem>() // 선택한 필터
    val selectedFilter: MutableLiveData<GalleryFilterItem>
        get() = _selectedFilter

    private val _selectedPhotoList = ListMutableLiveData<GallerySelectedItem>() // 선택한 사진들
    val selectedPhotoList: ListMutableLiveData<GallerySelectedItem>
        get() = _selectedPhotoList

    fun resetFilter() {
        _filterList.value.map { it.isSelected = false }
    }

    fun selectedFilter(id: String) {
        _filterList.value.forEach {
            if (it.bucketId == id) {
                it.isSelected = true
                selectedFilter.value = it
                return@forEach

            }
        }
    }

    fun start() {
        fetchFilter()

        provider.contentResolver
            .registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver
            )
    }

    /**
     * 갤러리 모든 앨범 가져오는 함수.
     *
     * @author hmju
     */
    @SuppressLint("Recycle")
    private fun fetchFilter() {
        launch {
            Flowable.fromCallable {
                val filterList = provider.fetchGalleryFilter()
                var bucketId = "ALL"
                filterList.find { it.isSelected }?.let { selectedItem ->
                    _selectedFilter.postValue(selectedItem)
                    bucketId = selectedItem.bucketId
                }
                _filterList.postValue(filterList)

                provider.fetchGallery(filterId = bucketId)
            }
                .compute()
                .ui()
                .doOnSubscribe { onLoading() }
                .subscribe({
                    JLogger.d("Third ${Thread.currentThread()}")
                    it?.let {
                        cursor.postValue(it)
                    }
                    onSuccess()
                    JLogger.d("onSuccess ")
                }, {
                    JLogger.d("Error ${it.message}")
                    onError()
                })
        }
    }

    /**
     * 갤러리 이미지 가져오기
     * 필터가 걸려있으면 자동으로 처리함.
     */
    fun fetchGallery() {
        // 선택된 이미지 초기화
        if (selectedPhotoList.size() > 0) {
            _selectedPhotoList.postClear()
        }

        launch {
            Flowable.just(
                provider.fetchGallery(filterId = selectedFilter.value!!.bucketId)
            ).compute()
                .ui()
                .doOnSubscribe {
                    // 기존 남아 있는 커서 Exit
                    cursor.value?.close()
                    onLoading()
                }
                .subscribe({ _cursor ->
                    _cursor?.let {
                        cursor.postValue(it)
                    }
                    onSuccess()
                }, {
                    onError()
                })
        }
    }

    fun moveCamera() {
        startCamera.call()
    }

    fun moveSubmit() {
        startSubmit.call()
    }

    fun showFilterSheet() {
        startFilter.call()
    }


    /**
     * 갤러리에서 사진 선택 및 해제시 호출하는 함수.
     * @param pos -> RecyclerView 기준 포지션
     * @param id -> 갤러리 콘텐츠 아이디
     */
    fun onSelect(pos: Int, id: String) {
        // 선택된 이미지 검사.
        val item = GallerySelectedItem(id = id, pos = pos)
        if (selectedPhotoList.contains(item)) {
            _selectedPhotoList.postRemove(item)

            // RecyclerView 갱신 처리.
            startNotify.value = item
        } else {
            // 사진 추가.
            if (limitImageSize > selectedPhotoList.size()) {
                _selectedPhotoList.postAdd(item)

                // RecyclerView 갱신 처리.
                startNotify.value = item
            } else {
                // 파일 업로드 최대 개수 제한.
                startToast.value = resProvider.getString(R.string.str_info_file_max_cnt)
            }
        }
    }

    /**
     * Test 를 위한 함수.
     * 이미지 편집 페이지 진입 함수.
     */
    fun moveImageEdit(id: String) {
        startImageEdit.value = id
    }

    /**
     * 선택한 이미지인지 검사.
     */
    fun isSelected(id: String): Boolean {
        return if (selectedPhotoList.size() > 0) {
            synchronized(tmpGallerySelectItem) {
                tmpGallerySelectItem.id = id
                return selectedPhotoList.contains(tmpGallerySelectItem)
            }
        } else {
            false
        }
    }

    override fun onCleared() {
        // contentObserver 해제.
        provider.contentResolver.unregisterContentObserver(contentObserver)
        super.onCleared()
    }

    /**
     * Convenience extension method to register a [ContentObserver] given a lambda.
     */
//    private fun ContentResolver.registerObserver(
//        uri: Uri,
//        observer: (selfChange: Boolean) -> Unit
//    ): ContentObserver {
//        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean) {
//                observer(selfChange)
//            }
//        }
//        registerContentObserver(uri, true, contentObserver)
//        return contentObserver
//    }
}