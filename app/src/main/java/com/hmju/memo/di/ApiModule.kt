package com.hmju.memo.di

import com.hmju.memo.utils.createOkHttpClient
import com.hmju.memo.utils.headerInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module

/**
 * Description: Http 통신 APi 모듈.
 *
 * Created by juhongmin on 2020/05/30
 */

val apiModule = module{
    single{
        createOkHttpClient<OkHttpClient>(headerInterceptor())
    }
}