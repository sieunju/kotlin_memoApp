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
    @PrimaryKey(autoGenerate = true) val manageNo: Int,
    @ColumnInfo(name = "TAG") val tag: String,
    @ColumnInfo(name = "TITLE") val title: String,
    @ColumnInfo(name= "CONTENTS") val contents: String,
    @ColumnInfo(name="REGISTER_DATE") val date: Date
)