package com.hmju.memo.repository.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

/**
 * Description :
 *
 * Created by juhongmin on 12/17/20
 */
@Entity(
    tableName = "MEMO_IMG",
    foreignKeys = [
        ForeignKey(
            entity = Memo::class,
            parentColumns = ["manageNo"],
            childColumns = ["MEMO_ID"],
            onDelete = CASCADE
        )]
)
data class MemoImage(
    @PrimaryKey(autoGenerate = true) val manageNo: Int? = null,
    @ColumnInfo(name = "MEMO_ID") val memoId: Int,
    @ColumnInfo(name = "IMG_PATH") val path: String,
    @ColumnInfo(name = "REGISTER_DATE") val date: Long? = System.currentTimeMillis()
)