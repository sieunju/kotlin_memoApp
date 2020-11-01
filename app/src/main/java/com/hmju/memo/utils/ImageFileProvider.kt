package com.hmju.memo.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.hmju.memo.define.Etc
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Description : File Resource Provider
 *
 * Created by juhongmin on 2020/10/11
 */
interface ImageFileProvider {
    val contentResolver: ContentResolver
    fun getFilePart(path: String?): Pair<MediaType, File>?
    fun createMultiPartBody(path: String?): Pair<RequestBody, File>?
    fun createTempFile(): File
    fun bitmapToFile(bitmap: Bitmap): File?
    fun sendPicture(path: String?): Boolean
    fun deleteFile(path: String?): Boolean
    fun deleteFile(file: File?): Boolean
}

class ImageFileProviderImpl(private val ctx: Context) : ImageFileProvider {
    private val MAX_IMAGE_WIDTH = 3000

    override val contentResolver: ContentResolver = ctx.contentResolver

    /**
     * Multipart 로 보낼 파일 처리 함수.
     *
     * @param path 이미지 파일 경로
     * @return ImageFile Format Type, Image File
     */
    override fun getFilePart(path: String?): Pair<MediaType, File>? {
        // 변수 유효성 체크.
        if (path.isNullOrEmpty()) return null
        val uri = Uri.parse(path)

        var bitmap: Bitmap
        // Scoped Storage
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            JLogger.d("Scoped Storage 처리")
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(contentResolver, uri)
            )
        } else {
            JLogger.d("Legacy 처리")
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        }

        // 이미지 회전 이슈 처리
        val matrix = Matrix()
        contentResolver.openInputStream(uri)?.let {
            val exif = ExifInterface(it)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            setRotate(
                orientation = orientation,
                matrix = matrix
            )

            it.close()
        }

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // 이미지 리사이징 처리.
        if (MAX_IMAGE_WIDTH < bitmap.width) {
            // 비율에 맞게 높이값 계산
            val height = MAX_IMAGE_WIDTH * bitmap.height / bitmap.width
            bitmap = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_WIDTH, height, true)
        }

        // Create Temp File
        bitmapToFile(bitmap)?.let {
            return Pair(getMimeType(it.path) ?: Etc.IMG_MIME_TYPE_FILE_EXTENSION.toMediaType(), it)
        } ?: return null
    }

    /**
     * Multipart 전송에 필요한 Body 및 File 값 생성 함수.
     * 전송 완료후 File 은 삭제 해야함.
     *
     * @param path File 위치값.
     */
    override fun createMultiPartBody(path: String?): Pair<RequestBody, File>? {
        // 변수 유효성 체크.
        if (path.isNullOrEmpty()) return null
        val uri = Uri.parse(path)

        var bitmap: Bitmap
        // Scoped Storage
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            JLogger.d("Scoped Storage 처리")
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(contentResolver, uri)
            )
        } else {
            JLogger.d("Legacy 처리")
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        }

        // 이미지 회전 이슈 처리
        val matrix = Matrix()
        contentResolver.openInputStream(uri)?.let {
            val exif = ExifInterface(it)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            setRotate(
                orientation = orientation,
                matrix = matrix
            )

            it.close()
        }

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // 이미지 리사이징 처리.
        if (MAX_IMAGE_WIDTH < bitmap.width) {
            // 비율에 맞게 높이값 계산
            val height = MAX_IMAGE_WIDTH * bitmap.height / bitmap.width
            bitmap = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_WIDTH, height, true)
        }

        bitmapToFile(bitmap)?.let { file ->
            return Pair(
                file.asRequestBody(
                    contentType = getMimeType(file.path)
                        ?: Etc.IMG_MIME_TYPE_FILE_EXTENSION.toMediaType()
                ),
                file
            )
        } ?: return null
    }

    /**
     * Create Temp ImageFile
     * @return File
     */
    @SuppressLint("SimpleDateFormat")
    override fun createTempFile(): File {
        val fileName = "tmp_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_"
        return File.createTempFile(
            fileName,
            Etc.IMG_FILE_EXTENSION,
            ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    /**
     * Convert Bitmap -> File
     * @param bitmap Image Bitmap
     * @return File JPEG
     */
    override fun bitmapToFile(bitmap: Bitmap): File? {
        val file = createTempFile()
        var fos: FileOutputStream? = null
        return try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            file
        } catch (ex: IOException) {
            JLogger.e("getTempFile Error ${ex.message}")
            if (file.isFile && file.delete()) {
                JLogger.e("Error File Delete")
            }
            null
        } finally {
            fos?.flush()
            fos?.close()
        }
    }

    /**
     * Delete File
     *
     * @param path File Path
     * @return true -> Delete Success, false -> Delete Fail
     */
    override fun deleteFile(path: String?): Boolean {
        path?.let {
            return try {
                File(it).delete()
            } catch (ex: IOException) {
                JLogger.e("Delete File Error ${ex.message}")
                false
            }
        } ?: return false
    }

    /**
     * Delete File
     *
     * @param file File
     * @return true -> Delete Success, false -> Delete Fail
     */
    override fun deleteFile(file: File?): Boolean {
        file?.let {
            return try {
                it.delete()
            } catch (ex: IOException) {
                JLogger.e("Delete File Error ${ex.message}")
                false
            }
        } ?: return false
    }

    @SuppressLint("SimpleDateFormat")
    override fun sendPicture(path: String?): Boolean {
        // Path 유효성 검사
        if (path.isNullOrEmpty()) return false

        val values = ContentValues().apply {
            // 이미지 이름.
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "Memo_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}${Etc.IMG_FILE_EXTENSION}"
            )
            // 이미지 타입.
            put(MediaStore.Images.Media.MIME_TYPE, Etc.IMG_MIME_TYPE_FILE_EXTENSION)

            // 버전 별로 분기 처리.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

                // 외부 앱에서 해당 파일 잠시 접근 제한 후 해제.
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val item: Uri? =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            item?.let { uri ->
                val pdf = contentResolver.openFileDescriptor(uri, "w", null)
                pdf?.let {
                    val file = File(path)
                    val fos = FileOutputStream(it.fileDescriptor)
                    fos.write(file.readBytes())
                    fos.close()

                    values.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    }

                    contentResolver.update(uri, values, null, null)

                    file.delete()
                    return true
                }
            } ?: return false
        } catch (ex: Exception) {
            JLogger.e("Error ${ex.message}")
            return false
        }
    }

    private fun getMimeType(path: String): MediaType? {
        val extension = path.substring(path.lastIndexOf(".")).toLowerCase(Locale.ROOT)
        val typeMap = MimeTypeMap.getFileExtensionFromUrl(extension)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(typeMap)
        mimeType?.let {
            return it.toMediaType()
        } ?: run {
            return null
        }
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
}