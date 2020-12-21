package com.hmju.memo.di

import com.hmju.memo.repository.DataSource
import com.hmju.memo.repository.DataSourceImpl
import com.hmju.memo.repository.db.AppDataBase
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

    single<DataSource> {
        DataSourceImpl(
            loginManager = get(),
            imgFileProvider = get(),
            dataBase = get(),
            apiService = get()
        )
    }
}