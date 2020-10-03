package com.hmju.memo.viewModels

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.define.NetworkState
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.model.gallery.GallerySelectedItem
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Observable

/**
 * Description : 앨범 및 카메라 ViewModel Class
 *
 * Created by hmju on 2020-09-17
 */
class GalleryViewModel(
    private val limitImageSize: Int,
    private val provider: ResourceProvider
) : BaseViewModel() {

    companion object {
        const val DEFAULT_FILTER_ID = "ALL"
        const val DEFAULT_FILTER_NAME = "최근 항목"
        val tmpGallerySelectItem = GallerySelectedItem(id = "", pos = -1)
    }

    // 갤러리에서 사진이 추가 / 삭제 되는 경우 갱신 처리.
    private val contentObserver: ContentObserver by lazy {
        return@lazy object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                JLogger.d("onChange $selfChange")
                super.onChange(selfChange)
            }
        }
    }
    val cursor = MutableLiveData<Cursor>()
    val startCamera = SingleLiveEvent<Unit>()
    val startSubmit = SingleLiveEvent<Unit>()
    val startFilter = SingleLiveEvent<Unit>()
    val startNotify = SingleLiveEvent<GallerySelectedItem>()

    private val _filterList = ListMutableLiveData<GalleryFilterItem>().apply {
        add(GalleryFilterItem(DEFAULT_FILTER_ID, DEFAULT_FILTER_NAME, true))
    } // 필터 영역
    val filterList: ListMutableLiveData<GalleryFilterItem>
        get() = _filterList
    val selectedFilter = NonNullMutableLiveData(
        GalleryFilterItem(
            id = DEFAULT_FILTER_ID,
            name = DEFAULT_FILTER_NAME,
            isSelected = true
        )
    ) // 선택한 필터

    private val _selectedPhotoList = ListMutableLiveData<GallerySelectedItem>() // 선택한 사진들
    val selectedPhotoList: ListMutableLiveData<GallerySelectedItem>
        get() = _selectedPhotoList

    fun resetFilter() {
        _filterList.value.map { it.isSelected = false }
    }

    fun selectedFilter(id: String) {
        _filterList.value.forEach {
            if (it.id == id) {
                it.isSelected = true
                selectedFilter.value = it
                return@forEach

            }
        }
    }

    fun start() {
        fetchFilter()

        provider
            .getContentResolver()
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
        // 로딩바 노출
        startNetworkState.postValue(NetworkState.LOADING)

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val selection = StringBuilder()
        val selectionArgs = arrayListOf<String>()

        // 데이터 하나만 가져오도록... 비용 최소화
        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT 1"

        launch {
            Observable.create<GalleryFilterItem> {
                try {
                    loop@ while (true) {

//                        JLogger.d("===================================================")
//                        JLogger.d("TEST:: Selection $selection")
//                        JLogger.d("TEST:: Argument $selectionArgs")
//                        JLogger.d("===================================================")

                        val cursor = provider.getContentResolver().query(
                            uri,
                            projection,
                            if (selection.isEmpty()) null else selection.toString(),
                            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
                            sort
                        ) ?: break@loop

                        if (cursor.moveToLast()) {
                            val id =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                            val bucketId =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                            val bucketName =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                            if (!cursor.isClosed) {
                                cursor.close()
                            }

                            if (selection.isEmpty()) {
                                selection.append("${MediaStore.Images.Media.BUCKET_ID} !=?")
                            } else {
                                selection.append(" AND ")
                                selection.append("${MediaStore.Images.Media.BUCKET_ID} !=?")
                            }
                            selectionArgs.add(bucketId)

                            it.onNext(
                                GalleryFilterItem(
                                    id = bucketId,
                                    name = bucketName,
                                    isSelected = false,
                                    contentId = id
                                )
                            )
                        } else {
                            if (!cursor.isClosed) {
                                cursor.close()
                            }
                            break@loop
                        }
                    }

                    it.onComplete()
                } catch (ex: Exception) {
                    JLogger.d("Try Catch Error ${ex.message}")
                    it.onError(ex)
                    it.onComplete()
                }
            }
                .doOnError {
                    JLogger.d("Error ${it.message}")

                }
                .doOnNext {
//                    JLogger.d("onNext $it")
                    _filterList.value.add(it)
                }
                .doOnComplete {
                    JLogger.d("onComplete !")
                    fetchGallery()
                }
                .single()
                .subscribe()
        }
    }

    /**
     * 갤러리 이미지 가져오기
     * 필터가 걸려있으면 자동으로 처리함.
     */
    fun fetchGallery() {

        // 선택된 이미지 초기화
        if(selectedPhotoList.size() > 0) {
            _selectedPhotoList.postClear()
        }

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val selection = "${MediaStore.Images.Media.BUCKET_ID} ==?"

        // 기존 남아 있는 커서 Exit
        cursor.value?.close()
        cursor.postValue(
            provider.getContentResolver().query(
                uri,
                projection,
                if (selectedFilter.value.id == DEFAULT_FILTER_ID) null else selection,
                if (selectedFilter.value.id == DEFAULT_FILTER_ID) null else arrayOf(selectedFilter.value.id),
                sort
            )
        )
        startNetworkState.postValue(NetworkState.SUCCESS)
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
        if(selectedPhotoList.contains(item)) {
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
                startToast.value = provider.getString(R.string.str_info_file_max_cnt)
            }
        }
    }

    /**
     * 선택한 이미지인지 검사.
     */
    fun isSelected(id: String): Boolean {
        return if (selectedPhotoList.size() > 0) {
            synchronized(tmpGallerySelectItem) {
                JLogger.d("isSelected $id")
                tmpGallerySelectItem.id = id
                return selectedPhotoList.contains(tmpGallerySelectItem)
            }
        } else {
            false
        }
    }

    override fun onCleared() {
        // contentObserver 해제.
        provider.getContentResolver().unregisterContentObserver(contentObserver)
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