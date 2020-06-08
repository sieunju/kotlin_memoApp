package com.hmju.memo.di

import com.hmju.memo.repository.network.ApiRemoteDataSource
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.utils.createOkHttpClient
import com.hmju.memo.utils.createRetrofit
import com.hmju.memo.utils.forbiddenInterceptor
import com.hmju.memo.utils.headerInterceptor
import okhttp3.OkHttpClient
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
        createOkHttpClient(get())
    }

    // Retrofit2
    single<ApiService> {
        ApiRemoteDataSource(
            createRetrofit(get()),
            get()
        )
    }
}