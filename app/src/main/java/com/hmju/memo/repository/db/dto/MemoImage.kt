package com.hmju.memo.repository.db.dto

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

/**
 * Description :
 *
 * Created by juhongmin on 12/17/20
 */
@Entity(tableName = "MEMO_IMG")
data class MemoImage(
    @PrimaryKey(autoGenerate = true) val manageNo: Int,
    @ColumnInfo(name = "MEMO_ID") val memoId : Int,
    @ColumnInfo(name = "IMG_PATH") val path: String,
    @ColumnInfo(name = "REGISTER_DATE") val date : Date
)