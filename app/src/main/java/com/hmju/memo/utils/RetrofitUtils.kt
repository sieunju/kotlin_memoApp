package com.hmju.memo.utils

import com.hmju.memo.preferences.CommonPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Description: Retrofit Utils Class
 *
 * Created by juhongmin on 2020/05/30
 */

inline fun <reified T> createOkHttpClient(interceptor: Interceptor): OkHttpClient {
    val retrofitLogger = RetrofitLogger()
    retrofitLogger.setLevel(RetrofitLogger.Level.BODY)
    return OkHttpClient.Builder()
        .build()
}

val REQUEST_ACCEPT: String = "accept"
val VALUE_ACCEPT: String = "application/json"

/**
 * 비로그인 상태 HeaderInterceptor Class
 */
fun headerInterceptor() : Interceptor {
    return Interceptor {chain ->
        val origin: Request = chain.request()

        val request: Request = origin.newBuilder()
            .header(REQUEST_ACCEPT, VALUE_ACCEPT)
            .method(origin.method(),origin.body())
            .build()
        chain.proceed(request)
    }
}


