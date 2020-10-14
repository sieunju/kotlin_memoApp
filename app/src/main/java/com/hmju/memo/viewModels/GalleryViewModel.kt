package com.hmju.memo.viewModels

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.LiveData
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
import com.hmju.memo.utils.ImageFileProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.*

/**
 * Description : 앨범 및 카메라 ViewModel Class
 *
 * Created by hmju on 2020-09-17
 */
class GalleryViewModel(
    private val limitImageSize: Int,
    val provider: ImageFileProvider,
    private val resProvider: ResourceProvider
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
                super.onChange(selfChange)
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
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val selection = StringBuilder()
        val selectionArgs = arrayListOf<String>()
        val sort = "${MediaStore.Images.Media.DATE_TAKEN} DESC "

        var prevPhotoUri: String = ""
        var prevBucketId: String = ""
        var prevBucketName: String = ""
        var prevSize: Int = -1

        launch {
            Observable.create<GalleryFilterItem> {
                try {
                    loop@ while (true) {

                        val cursor = provider.contentResolver.query(
                            uri,
                            projection,
                            if (selection.isEmpty()) null else selection.toString(),
                            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
                            sort
                        ) ?: break@loop

                        if (cursor.moveToLast()) {
                            val contentId =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                            val photoUri = Uri.withAppendedPath(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentId
                            ).toString()
                            val bucketId =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                            val bucketName =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                            val size = cursor.count

                            if (!cursor.isClosed) {
                                cursor.close()
                            }

                            // Where Setting
                            if (selection.isNotEmpty()) {
                                selection.append(" AND ")
                            }

                            selection.append(MediaStore.Images.Media.BUCKET_ID)
                            selection.append(" !=?")
                            selectionArgs.add(bucketId)

                            // 앨범명 유효성 검사.
                            if (prevBucketId.isNotEmpty()) {
                                val diffSize = prevSize - size
                                prevSize = size
                                it.onNext(
                                    GalleryFilterItem(
                                        bucketId = prevBucketId,
                                        bucketName = prevBucketName,
                                        photoUri = prevPhotoUri,
                                        size = diffSize,
                                        isSelected = false
                                    )
                                )
                            }

                            // 초기값 세팅
                            if (prevSize == -1) {
                                prevSize = size
                                it.onNext(
                                    GalleryFilterItem(
                                        bucketId = DEFAULT_FILTER_ID,
                                        bucketName = DEFAULT_FILTER_NAME,
                                        photoUri = photoUri,
                                        size = size,
                                        isSelected = true
                                    )
                                )
                            }

                            // Set Data.
                            prevPhotoUri = photoUri
                            prevBucketId = bucketId
                            prevBucketName = bucketName

                        } else {
                            // 맨 마지막 앨범 추가
                            if (prevSize != 0) {
                                it.onNext(
                                    GalleryFilterItem(
                                        bucketId = prevBucketId,
                                        bucketName = prevBucketName,
                                        photoUri = prevPhotoUri,
                                        size = prevSize,
                                        isSelected = false
                                    )
                                )
                            }

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
                .subscribeOn(computation())
                .doOnSubscribe { onLoading() }
                .doOnError {
                    JLogger.d("Error ${it.message}")
                }
                .observeOn(newThread())
                .doOnNext {
//                    JLogger.d("onNext $it")
                    if (it.isSelected) {
                        selectedFilter.postValue(it)
                    }
                    _filterList.value.add(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    fetchGallery()
                }
                .subscribe()
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

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val selection = "${MediaStore.Images.Media.BUCKET_ID} ==?"

        val isAll: Boolean = selectedFilter.value?.bucketId == DEFAULT_FILTER_ID

        // 기존 남아 있는 커서 Exit
        cursor.value?.close()
        launch {
            Observable.just(
                cursor.postValue(
                    provider.contentResolver.query(
                        uri,
                        projection,
                        if (isAll) null else selection,
                        if (isAll) null else arrayOf(selectedFilter.value!!.bucketId),
                        sort
                    )
                )
            )
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onLoading() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { onSuccess() }
                .subscribe()
        }


        onSuccess()
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