package com.hmju.memo.utils

import com.hmju.memo.define.NetInfo
import com.hmju.memo.repository.preferences.AccountPref
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 서버 에러 코드에 대한 처리 Interceptor 에서 걸러낸다.
 */
fun forbiddenInterceptor() :Interceptor{
    return Interceptor {chain: Interceptor.Chain ->
        val response: Response = chain.proceed(chain.request())
        when(response.code()){
            NetInfo.CODE_BAD_REQUEST -> {
                JLogger.e("서버 에러 발생 CODE_BAD_REQUEST")
            }
            NetInfo.CODE_FORBIDDEN -> {
                JLogger.e("서버 에러 발생 CODE_FORBIDDEN")
            }
            NetInfo.CODE_NOT_FOUND -> {
                JLogger.e("서버 에러 발생 CODE_NOT_FOUND")
            }
            NetInfo.CODE_ERROR -> {
                JLogger.e("서버 에러 발생 CODE_ERROR")
                throw Exception("서버가 닫혔습니다.")
            }
            NetInfo.CODE_BAD_GATE_WAY -> {
                JLogger.e("서버 에러 발생 CODE_BAD_GATE_WAY")
                throw Exception("서버가 닫혔습니다.")
            }
            else -> {}
        }
        response
    }
}

/**
 * 헤더 값 세팅 하는 부분
 */
fun headerInterceptor(pref: AccountPref) : Interceptor {
    return Interceptor { chain ->
        val origin: Request = chain.request()

        val request: Request = origin.newBuilder()
            .header("accept", "application/json")
            .header("Content-Type","application/json")
            .header(NetInfo.KEY_LOGIN,pref.getLoginKey())
            .method(origin.method(),origin.body())
            .build()
        chain.proceed(request)
    }
}

/**
 * create Client.
 */
fun createOkHttpClient(interceptor: Interceptor): OkHttpClient{
    val retrofitLogger = RetrofitLogger()
    retrofitLogger.setLevel(RetrofitLogger.Level.BODY)
    return OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5,TimeUnit.SECONDS)
        .writeTimeout(5,TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
        .addInterceptor(forbiddenInterceptor())
        .addInterceptor(interceptor)
        .addInterceptor(retrofitLogger)
        .build()
}

/**
 * Http 통신하는 Retrofit 생성.
 */
inline fun <reified T> createRetrofit(client: OkHttpClient) : T{
    val retrofit = Retrofit.Builder()
        .baseUrl(NetInfo.BASE_URL)
        .client(client)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}




