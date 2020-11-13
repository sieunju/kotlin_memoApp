package com.hmju.memo.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.hmju.memo.define.Etc
import com.hmju.memo.model.gallery.GalleryFilterItem

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

    companion object {
        val CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val ID = MediaStore.Images.Media._ID

        @SuppressLint("InlinedApi")
        val BUCKET_ID = MediaStore.Images.Media.BUCKET_ID

        @SuppressLint("InlinedApi")
        val BUCKET_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    }

    @SuppressLint("Recycle")
    override fun fetchGalleryFilter(): ArrayList<GalleryFilterItem> {
        val dataList = arrayListOf<GalleryFilterItem>()
        val projection = arrayOf(
            ID,
            BUCKET_ID,
            BUCKET_NAME
        )
        val selection = StringBuilder()
        val selectionArgs = arrayListOf<String>()
        val sort = "$ID DESC "

        var prevPhotoUri: String = ""
        var prevBucketId: String = ""
        var prevBucketName: String = ""
        var prevCount: Int = -1

        try {
            loop@ while (true) {
                val cursor = contentResolver.query(
                    CONTENT_URI,
                    projection,
                    if (selection.isEmpty()) null else selection.toString(),
                    if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
                    sort
                ) ?: break@loop

                if (cursor.moveToLast()) {
                    val contentId = cursor.getString(cursor.getColumnIndex(ID))
                    val photoUri = Uri.withAppendedPath(CONTENT_URI, contentId).toString()
                    val bucketId = cursor.getString(cursor.getColumnIndex(BUCKET_ID))
                    val bucketName = cursor.getString(cursor.getColumnIndex(BUCKET_NAME))
                    val count = cursor.count

                    if (!cursor.isClosed) {
                        cursor.close()
                    }

                    // Where Setting
                    if (selection.isNotEmpty()) {
                        selection.append(" AND ")
                    }

                    selection.append(BUCKET_ID)
                    selection.append(" !=?")
                    selectionArgs.add(bucketId)

                    // 앨범명 유효성 검사.
                    if (prevBucketId.isNotEmpty()) {
                        val diffCount = prevCount - count
                        prevCount = count
                        dataList.add(
                            GalleryFilterItem(
                                bucketId = prevBucketId,
                                bucketName = prevBucketName,
                                photoUri = prevPhotoUri,
                                count = diffCount,
                                isSelected = false
                            )
                        )
                    }

                    // 초기값 세팅
                    if (prevCount == -1) {
                        prevCount = count
                        dataList.add(
                            GalleryFilterItem(
                                bucketId = Etc.DEFAULT_GALLERY_FILTER_ID,
                                bucketName = Etc.DEFAULT_GALLERY_FILTER_NAME,
                                photoUri = photoUri,
                                count = count,
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
                    if (prevCount != 0) {
                        dataList.add(
                            GalleryFilterItem(
                                bucketId = prevBucketId,
                                bucketName = prevBucketName,
                                photoUri = prevPhotoUri,
                                count = prevCount,
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
        val projection = arrayOf(ID)

        val sort = "$ID DESC"
        val selection = "$BUCKET_ID ==?"

        val isAll: Boolean = filterId == Etc.DEFAULT_GALLERY_FILTER_ID
        return contentResolver.query(
            CONTENT_URI,
            projection,
            if (isAll) null else selection,
            if (isAll) null else arrayOf(filterId),
            sort
        )
    }
}
