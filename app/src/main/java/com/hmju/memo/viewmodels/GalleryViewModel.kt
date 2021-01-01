package com.hmju.memo.viewmodels

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extension.*
import com.hmju.memo.define.Etc
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.utils.CursorProvider
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Flowable
import java.lang.ref.WeakReference

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
        val LAST_EXECUTE_DELAY: Long = 500
        val MSG_GALLERY_UPDATE = 1
    }

    @SuppressLint("HandlerLeak")
    inner class LastExecute(viewModel: BaseViewModel) : Handler() {
        private val weakRef : WeakReference<BaseViewModel> = WeakReference<BaseViewModel>(viewModel)

        override fun handleMessage(msg: Message) {
            if(weakRef.get() == null ) return

            if (msg.what == MSG_GALLERY_UPDATE) {
                fetchFilter()
            }
        }
    }

    // 갤러리에서 사진이 추가 / 삭제 되는 경우 갱신 처리.
    private val contentObserver: ContentObserver by lazy {
        return@lazy object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                lastExecute.removeMessages(MSG_GALLERY_UPDATE)
                lastExecute.sendEmptyMessageDelayed(MSG_GALLERY_UPDATE, LAST_EXECUTE_DELAY)
            }
        }
    }
    val cursor = MutableLiveData<Cursor>()
    val startCamera = SingleLiveEvent<Unit>()
    val startSubmit = SingleLiveEvent<Unit>()
    val startFilter = SingleLiveEvent<Unit>()
    val startImageEdit = SingleLiveEvent<String>()
    val lastExecute = LastExecute(this)

    private val _filterList = ListMutableLiveData<GalleryFilterItem>() // 필터 영역
    val filterList: ListMutableLiveData<GalleryFilterItem>
        get() = _filterList
    private val _selectedFilter = MutableLiveData<GalleryFilterItem>() // 선택한 필터
    val selectedFilter: MutableLiveData<GalleryFilterItem>
        get() = _selectedFilter

    private val _selectedPhotoList = ListMutableLiveData<String>()// 선택한 사진들
    val selectedPhotoList: ListMutableLiveData<String>
        get() = _selectedPhotoList

    fun start() {
        fetchFilter()

        provider.contentResolver
            .registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver
            )
    }

    fun selectedFilter(item: GalleryFilterItem): Boolean {
        if (item != selectedFilter.value) {
            _selectedFilter.value = item
            return true
        }
        return false
    }

    /**
     * 갤러리 모든 앨범 가져오는 함수.
     *
     * @author hmju
     */
    private fun fetchFilter() {
        launch {
            Flowable.fromCallable {
                val filterList = provider.fetchGalleryFilter()
                // 초기값 세팅.
                filterList.find { it.bucketId == Etc.DEFAULT_GALLERY_FILTER_ID }
                    ?.let { selectedItem ->
                        _selectedFilter.postValue(selectedItem)
                    }
                _filterList.postValue(filterList)

                provider.fetchGallery(filterId = Etc.DEFAULT_GALLERY_FILTER_ID)
            }
                .compute()
                .nextUi()
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
        launch {
            Flowable.just(
                provider.fetchGallery(filterId = selectedFilter.value!!.bucketId)
            ).compute()
                .nextUi()
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

    fun onSelect(id: String) {
        onSelect(null, id)
    }

    /**
     * 갤러리에서 사진 선택 및 해제시 호출하는 함수.
     * @param id -> 갤러리 콘텐츠 아이디
     */
    fun onSelect(view: View?, id: String) {
        // 선택한 이미지가 있는 경우.
        if (selectedPhotoList.contains(id)) {
            selectedPhotoList.postRemove(id)

            view?.visibility = View.GONE
        } else {
            // 사진 추가 가능한지 여부
            if (limitImageSize > selectedPhotoList.size()) {
                _selectedPhotoList.postAdd(id)
                view?.visibility = View.VISIBLE
            } else {
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
        return if (selectedPhotoList.size() == 0) {
            false
        } else {
            selectedPhotoList.contains(id)
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