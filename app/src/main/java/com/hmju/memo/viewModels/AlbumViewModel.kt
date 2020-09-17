package com.hmju.memo.viewModels

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.convenience.ListMutableLiveData
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

    val cursor = MutableLiveData<Cursor>()
    val selectedPhotoList = ListMutableLiveData<String>()

    fun start() {
        cursor.postValue(photoCursor())
    }

    fun photoCursor(): Cursor? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
        val sortOrderDesc = MediaStore.Images.Media.DATE_ADDED + " desc"

        return provider.getContentResolver()
            .query(uri, projection, null, null, sortOrderDesc)
    }

    fun getRequestBody(part: String): RequestBody =
        part.toRequestBody()

    fun getImageMultiPart(files: ListMutableLiveData<String>): List<MultipartBody.Part> {
        val parts = arrayListOf<MultipartBody.Part>()
        files.value.forEach { path ->
            getFileInfo(path)?.let { file ->

                val requestBody = file.asRequestBody("image/jpg".toMediaType())
                parts.add(
                    MultipartBody.Part.createFormData(
                        "file", file.name, requestBody
                    )
                )
            }
        }
        return parts
    }

    fun getFileInfo(filePath: String): File? {
        var bitmap: Bitmap? = null
        // Android 10 Scoped Storage 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // BitMap 및 리사이징
            try {
                bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        provider.getContentResolver(),
                        Uri.parse(filePath)
                    )
                )
                var tmpWidth = bitmap.width
                var tmpHeight = bitmap.height

//                // 기준 너비보다 큰경우 비율에 맞게 리사이징
//                if (750 < tmpWidth) {
//                    // 비율에 맞게 높이값 계산
//                    val resizeHeight: Int = 750 * tmpHeight / tmpWidth
//                    tmpWidth = 750
//                    tmpHeight = resizeHeight
//                    // 이미지 재 설정
//                    bitmap = Bitmap.createScaledBitmap(bitmap, tmpWidth, tmpHeight, true)
//                }
            } catch (ex: Exception) {
                JLogger.d("TEST:: uploadFile Bitmap Error\t" + ex.message)
            }
        } else {
            JLogger.d("TEST:: 그 이하 버전입니다!")
            var inputStream: InputStream? = null
            try {
                bitmap = BitmapFactory.decodeStream(
                    provider.getContentResolver().openInputStream(Uri.parse(filePath))
                )

                // 자동 회전 현상 방지 로직
                val matrix = Matrix()
                inputStream = provider.getContentResolver().openInputStream(Uri.parse(filePath))
                inputStream?.let {
                    val exif = ExifInterface(it)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    setRotate(orientation, matrix)
                }


                // 이미지 자동 회전 방지 로직.
                if (bitmap != null) {
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                }
            } catch (ex: Exception) {
                JLogger.d("TEST:: uploadFile Bitmap Error\t" + ex.message)
            } finally {
                inputStream?.let {
                    try {
                        it.close()
                    } catch (ignored: Exception) {
                    }
                }
            }
        }

        var file: File? = null
        var fos: FileOutputStream? = null

        try {
            file = File.createTempFile("temp_" + System.currentTimeMillis(), ".jpg")
            fos = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (ex: Exception) {
            JLogger.d("TEST:: TmpFile Create Error\t" + ex.message)
            // 파일 삭제.
            deleteFile(file)
        } finally {
            try {
                if (fos != null) {
                    fos.flush()
                    fos.close()
                }
            } catch (ex: Exception) {
                JLogger.d("TEST:: Fos Flush Error\t" + ex.message)
            }
        }

        return file
    }

    private fun setRotate(orientation: Int, matrix: Matrix): Boolean {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
                true
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
                true
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
                true
            }
            else -> false
        }
    }

    private fun deleteFile(file: File?) {
        if (file == null || !file.isFile) return
        if (file.delete()) {
            JLogger.d("File Delete Success")
        }
    }

    private fun getMimeType(path: String): String? {
        val extension = path.substring(path.lastIndexOf(".")).toLowerCase()
        val mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap)
    }
}