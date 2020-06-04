package com.hmju.memo.utils

import com.hmju.memo.define.NetInfo
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Description: Retrofit Utils Class
 *
 * Created by juhongmin on 2020/05/30
 */

inline fun <reified T> createOkHttpClient(interceptor: Interceptor): OkHttpClient {
    val retrofitLogger = RetrofitLogger()
    retrofitLogger.setLevel(RetrofitLogger.Level.BODY)
    return OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5,TimeUnit.SECONDS)
        .writeTimeout(5,TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
        .addInterceptor(interceptor)
        .build()
}

/**
 * 비로그인 상태 HeaderInterceptor Class
 */
fun headerInterceptor() : Interceptor {
    return Interceptor {chain ->
        val origin: Request = chain.request()

        val request: Request = origin.newBuilder()
            .header("accept", "application/json")
            .method(origin.method(),origin.body())
            .build()
        chain.proceed(request)
    }
}

fun headerInterceptor(loginKey: String) : Interceptor {
    return Interceptor { chain ->
        val origin: Request = chain.request()

        val request: Request = origin.newBuilder()
            .header("accept", "application/json")
            .header(NetInfo.KEY_LOGIN,loginKey)
            .method(origin.method(),origin.body())
            .build()
        chain.proceed(request)
    }
}


