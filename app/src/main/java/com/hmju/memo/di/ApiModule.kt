package com.hmju.memo.di

import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.NetworkDataSource
import com.hmju.memo.repository.network.NetworkDataSourceImpl
import com.hmju.memo.repository.network.login.LoginManager
import com.hmju.memo.repository.network.login.LoginManagerImpl
import com.hmju.memo.utils.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Description: Http 통신 APi 모듈.
 *
 * Created by juhongmin on 2020/05/30
 */

val apiModule = module {

    // init 헤더
    single {
        headerInterceptor(get())
    }

    // client
    single {
        createHttpClient(get())
    }

    // API Network Service
    single<ApiService> {
        createRetrofit(get())
    }

    // Network Data Source
    single<NetworkDataSource> {
        NetworkDataSourceImpl(
            get(), get(), get(), get()
        )
    }

    // LoginManager
    single<LoginManager> {
        LoginManagerImpl(androidContext(), get(), get())
    }
}