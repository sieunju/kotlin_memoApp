package com.hmju.memo.repository.db

import android.content.Context
import androidx.room.*
import com.hmju.memo.repository.db.dao.MemoDao
import com.hmju.memo.repository.db.dao.MemoImgDao
import com.hmju.memo.repository.db.dto.Memo
import com.hmju.memo.repository.db.dto.MemoImage

/**
 * Description : Room Data Base
 *
 * Created by juhongmin on 12/17/20
 */
@Database(
    entities = [Memo::class, MemoImage::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
    abstract fun memoImgDao(): MemoImgDao

    companion object {

        fun instance(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java, "memo_database"
            ).build()
        }
    }
}