package com.hmju.memo.viewModels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.NonNullMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.convenience.single
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Maybe
import io.reactivex.Observable

/**
 * Description : 앨범 및 카메라 ViewModel Class
 *
 * Created by hmju on 2020-09-17
 */
class GalleryViewModel(
    private val manageNo: Int,
    private val provider: ResourceProvider
) : BaseViewModel() {

    val UPLOAD_FILE_MAX_CNT = 5
    val cursor = MutableLiveData<Cursor>()
    val selectedPhotoList = HashMap<Int, String>()
    val startCamera = SingleLiveEvent<Unit>()
    val startSubmit = SingleLiveEvent<Unit>()
    val startToast = SingleLiveEvent<String>()
    val startFilter = SingleLiveEvent<Unit>()
    val startTest = SingleLiveEvent<Unit>()
    val galleryFilter = LinkedHashSet<Pair<String, String>>()
    val renewalFilter = ArrayList<Pair<String, String>>().apply {
        add(Pair("all", "최근 항목"))
    }

    val filterGallery = NonNullMutableLiveData(LinkedHashSet<String>()).apply {
        value.add(provider.getString(R.string.str_gallery_all))
    }
    val selectedFilter = NonNullMutableLiveData(provider.getString(R.string.str_gallery_all))

    fun start() {
        cursor.postValue(photoCursor())
    }

    private fun photoCursor(): Cursor? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        // 정렬 순서.
        val sortOrderDesc = MediaStore.Images.Media.DATE_ADDED + " desc"
        return provider.getContentResolver()
            .query(uri, projection, null, null, sortOrderDesc)
    }

    private fun firstCursor(uri: Uri, projection: Array<String>, sort: String): Cursor? {
        return provider.getContentResolver().query(uri, projection, null, null, sort)
    }

    private fun renewalTest() {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val firstProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val firstSort = "${MediaStore.Images.Media.DATE_TAKEN} DESC limit 1"

        firstCursor(uri, firstProjection, firstSort)?.let {
            if (it.count > 0) {
                JLogger.d("TEST:: Count!! ${it.count}")
                if (it.moveToNext()) {
                    val id = it.getString(0)
                    val name = it.getString(1)
                    JLogger.d("TEST:: First $id\t$name")
                    renewalFilter.add(Pair(id, name))

                    it.close()
                } else {
                    it.close()
                }
            } else {
                JLogger.d("사진이 없는 경우...")
            }
            it.close()
        }
    }

    private fun renewalFilter(){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val selection = StringBuilder()
        selection.append("${MediaStore.Images.Media.BUCKET_ID} != ")
        val selectionArgs = arrayListOf<String>()
        selectionArgs.add(renewalFilter[0].first)

        val sort = "${MediaStore.Images.Media.DATE_TAKEN} DESC limit 1"

        Observable.create<Unit> {observable->
            try{
                val cursor = provider.getContentResolver().query(uri,projection,selection.toString(),selectionArgs.toArray() as Array<out String>,sort)
                cursor?.let{

                    if(it.moveToNext()) {
                        val id = it.getString(it.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                        val name = it.getString(it.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        renewalFilter.add(Pair(id,name))
                    }

                    it.close()
                }
            } catch (ex : Exception){
                JLogger.d("Error ${ex.message}")
            }
        }.doOnNext {

        }
    }

    private fun testCursor(): Cursor? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.TITLE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        return provider.getContentResolver().query(uri, projection, null, null, sortOrder)
    }

    private fun runTest() {
        testCursor()?.let { cs ->
            JLogger.d("TEST:: Size ${cs.count}")
            val time = System.currentTimeMillis()
            launch {
                Observable.create<Triple<String, String, String>> {
                    try {
                        if (cs.moveToLast()) {
                            do {
                                val first = cs.getString(0)
                                val second = cs.getString(1)
                                val third = cs.getString(2)

                                galleryFilter.add(Pair(second, third))

                                it.onNext(Triple(first, second, third))
                            } while (cs.moveToPrevious())
                            cs.close()
                            it.onComplete()
                        } else {
                            cs.close()
                            it.onComplete()
                        }
                    } catch (ex: Exception) {
                        JLogger.d("Error ${ex.message}")
                        cs.close()
                        it.onError(ex)
                    }
                }
                    .doOnComplete {
                        JLogger.d("TEST:: onCompleted Diff Time${System.currentTimeMillis() - time}")
                        JLogger.d("TEST:: Filter ${galleryFilter}")
                    }
                    .doOnError {
                        JLogger.d("TEST:: Error ${it.message}")
                    }
                    .doOnNext {
//                        JLogger.d("TEST:: Next\t ${it.first}\t${it.second}\t${it.third}")
                    }
                    .single()
                    .toList()
                    .subscribe({
                        JLogger.d("TEST:: subscribe Diff Time${System.currentTimeMillis() - time}")
                    }, {
                        JLogger.d("TEST:: subscribe Error ${it.message}")
                    })
            }

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

    fun testClick() {
        startTest.call()
//        runTest()
        renewalTest()
    }


    fun onSelect(pos: Int, path: String, view: View) {
        // 선택된 이미지 검사.
        if (selectedPhotoList.containsKey(pos)) {
            selectedPhotoList.remove(pos)

            view.visibility = View.GONE
        } else {
            // 선택된 이미지가 없는 경우 추가.
            if (UPLOAD_FILE_MAX_CNT > selectedPhotoList.size) {
                selectedPhotoList[pos] = path

                view.visibility = View.VISIBLE
            } else {
                // 파일 업로드 최대 개수 제한.
                startToast.value = provider.getString(R.string.str_info_file_max_cnt)
            }
        }
    }

    fun isSelected(pos: Int): Boolean {
        return selectedPhotoList.containsKey(pos)
    }
}