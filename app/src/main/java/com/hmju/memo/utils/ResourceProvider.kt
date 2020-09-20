package com.hmju.memo.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Description : ResourceProvider
 *
 * Created by juhongmin on 2020/09/05
 */
interface ResourceProvider {
    fun getContext(): Context
    fun getDrawable(@DrawableRes resId: Int): Drawable?
    fun getDimen(@DimenRes resId: Int): Int
    fun getColor(@ColorRes color: Int): Int
    fun getString(@StringRes resId: Int): String
    fun getStringArray(@ArrayRes resId: Int): Array<String>
    fun getContentResolver(): ContentResolver
    fun getImageFileContents(path: String): Pair<MediaType, File>?
    fun deleteFiles(fileList: List<File>)
}

class ResourceProviderImpl(private val ctx: Context) : ResourceProvider {
    private val res by lazy { ctx.resources }

    override fun getContext() = ctx

    override fun getDrawable(resId: Int) = AppCompatResources.getDrawable(ctx, resId)

    override fun getDimen(resId: Int) = res.getDimensionPixelSize(resId)

    override fun getColor(color: Int) = ContextCompat.getColor(ctx, color)

    override fun getString(resId: Int) = res.getString(resId)

    override fun getStringArray(resId: Int) = res.getStringArray(resId)

    override fun getContentResolver(): ContentResolver {
        return ctx.contentResolver
    }

    override fun getImageFileContents(imgPath: String): Pair<MediaType, File>? {
        val uriPath = Uri.parse(imgPath)
        var bitmap: Bitmap
        // 버전 별로 분기 처리 Scoped Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            JLogger.d("Scoped Storage 처리")
            bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    getContentResolver(),
                    uriPath
                )
            )

//            var tmpWidth = bitmap.width
//            var tmpHeight = bitmap.height
//
//            // 기준 너비보다 큰경우 비율에 맞게 리사이징
//            if (1000 < tmpWidth) {
//                // 비율에 맞게 높이값 계산
//                val resizeHeight: Int = 1000 * tmpHeight / tmpWidth
//                tmpWidth = 1000
//                tmpHeight = resizeHeight
//                // 이미지 재 설정
//                bitmap = Bitmap.createScaledBitmap(bitmap, tmpWidth, tmpHeight, true)
//            }

        } else {
            JLogger.d("Legacy 처리 ")
            bitmap = BitmapFactory.decodeStream(
                getContentResolver().openInputStream(
                    uriPath
                )
            )

            // 이미지 자동 회전 방지 로직
            val matrix = Matrix()
            getContentResolver().openInputStream(uriPath)?.let {
                JLogger.d("함수 호출! InputStream")
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

            JLogger.d("그후 여기를 탑니다.")
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        // Temp File Create
        val file = File.createTempFile("temp_" + System.currentTimeMillis(), ".png")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (ex: IOException) {
            JLogger.d("Fos Exception\t${ex.message}")
            if (file.isFile && file.delete()) {
                JLogger.d("File Delete Success")
            }
            return null
        } finally {
            fos?.flush()
            fos?.close()
        }

        // 파일 생성할떄 image/png 로 파일 생성하기 떄문에 콘텐츠 타입 하드로 생
        return Pair(getMimeType(file.path) ?: "image/png".toMediaType(), file)
    }

    override fun deleteFiles(fileList: List<File>) {
        fileList.forEach { it.delete() }
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