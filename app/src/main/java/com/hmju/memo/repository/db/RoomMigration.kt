package com.hmju.memo.repository.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hmju.memo.utils.JLogger

/**
 * Description : Room DataBase Migration Class
 *
 * Created by juhongmin on 12/20/20
 */
val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        JLogger.d("DataBaseMigration 1..2")
        database.execSQL("ALTER TABLE MEMO ADD TEST TEXT")
    }
}