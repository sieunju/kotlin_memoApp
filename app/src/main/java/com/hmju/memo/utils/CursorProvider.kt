package com.hmju.memo.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.hmju.memo.define.Etc
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.viewModels.GalleryViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

/**
 * Description : Cursor Provider
 *
 * Created by juhongmin on 2020/10/19
 */
interface CursorProvider {
    val contentResolver: ContentResolver
    fun fetchGalleryFilter(): ArrayList<GalleryFilterItem>
    fun fetchGallery(filterId: String): Cursor?
}

class CursorProviderImpl(ctx: Context) : CursorProvider {
    override val contentResolver: ContentResolver = ctx.contentResolver

    @SuppressLint("Recycle")
    override fun fetchGalleryFilter(): ArrayList<GalleryFilterItem> {
        val dataList = arrayListOf<GalleryFilterItem>()
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

        try {
            loop@ while (true) {
                val cursor = contentResolver.query(
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
                        dataList.add(
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
                        dataList.add(
                            GalleryFilterItem(
                                bucketId = Etc.DEFAULT_GALLERY_FILTER_ID,
                                bucketName = Etc.DEFAULT_GALLERY_FILTER_NAME,
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
                        dataList.add(
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
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return dataList
    }

    override fun fetchGallery(filterId: String): Cursor? {
        JLogger.d("FilterId $filterId")
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val sort = "${MediaStore.Images.Media._ID} DESC"
        val selection = "${MediaStore.Images.Media.BUCKET_ID} ==?"

        val isAll: Boolean = filterId == Etc.DEFAULT_GALLERY_FILTER_ID
        return contentResolver.query(
            uri,
            projection,
            if (isAll) null else selection,
            if (isAll) null else arrayOf(filterId),
            sort
        )
    }
}
