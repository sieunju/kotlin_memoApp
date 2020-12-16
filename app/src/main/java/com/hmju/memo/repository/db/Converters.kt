package com.hmju.memo.repository.db

import androidx.room.TypeConverter
import java.util.*

/**
 * Description : Room Database Converter
 *
 * Created by juhongmin on 12/17/20
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) : Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date) : Long {
        return date.time
    }
}