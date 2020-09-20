package com.hmju.memo.viewModels

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
import com.hmju.memo.convenience.SingleLiveEvent
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Description : 앨범 및 카메라 ViewModel Class
 *
 * Created by hmju on 2020-09-17
 */
class AlbumViewModel(
    private val manageNo: Int,
    private val provider: ResourceProvider
) : BaseViewModel() {

    val UPLOAD_FILE_MAX_CNT = 5
    val cursor = MutableLiveData<Cursor>()
    val selectedPhotoList = HashMap<Int,String>()
    val startCamera = SingleLiveEvent<Unit>()
    val startSubmit = SingleLiveEvent<Unit>()
    val startToast = SingleLiveEvent<String>()

    fun start() {
        cursor.postValue(photoCursor())
    }

    private fun photoCursor(): Cursor? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
        // 정렬 순서.
        val sortOrderDesc = MediaStore.Images.Media.DATE_ADDED + " desc"

        return provider.getContentResolver()
            .query(uri, projection, null, null, sortOrderDesc)
    }

    fun moveCamera(){
        startCamera.call()
    }

    fun moveSubmit(){
        startSubmit.call()
    }

    fun onSelect(pos : Int, path: String ,view: View) {
        // 선택된 이미지 검사.
        if(selectedPhotoList.containsKey(pos)) {
            selectedPhotoList.remove(pos)

            view.visibility = View.GONE
        } else {
            // 선택된 이미지가 없는 경우 추가.
            if(UPLOAD_FILE_MAX_CNT > selectedPhotoList.size) {
                selectedPhotoList[pos] = path

                view.visibility = View.VISIBLE
            } else {
                // 파일 업로드 최대 개수 제한.
                startToast.value = provider.getString(R.string.str_info_file_max_cnt)
            }
        }
    }

    fun isSelected(pos : Int) : Boolean {
        return selectedPhotoList.containsKey(pos)
    }
}