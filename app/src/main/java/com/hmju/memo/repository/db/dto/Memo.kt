package com.hmju.memo.repository.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Description : 메모 DataBase
 *
 * Created by juhongmin on 12/17/20
 */
@Entity(tableName = "MEMO")
data class Memo(
    @PrimaryKey(autoGenerate = true) val manageNo: Int? = null,
    @ColumnInfo(name = "TAG") val tag: Int,
    @ColumnInfo(name = "TITLE") val title: String,
    @ColumnInfo(name= "CONTENTS") val contents: String,
    @ColumnInfo(name="REGISTER_DATE") val date: Long? = System.currentTimeMillis()
)