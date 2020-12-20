package com.hmju.memo.di

import com.hmju.memo.repository.db.AppDataBase
import com.hmju.memo.repository.db.RoomDataSource
import com.hmju.memo.repository.db.RoomDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Description : Local Data Source Module
 *
 * Created by juhongmin on 12/20/20
 */
val dataModule = module {

    single {
        AppDataBase.instance(androidContext())
    }

    single<RoomDataSource> {
        RoomDataSourceImpl(get())
    }
}