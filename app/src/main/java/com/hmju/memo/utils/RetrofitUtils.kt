package com.hmju.memo.utils

import com.google.gson.*
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.hmju.memo.define.NetInfo
import com.hmju.memo.repository.preferences.AccountPref
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * 서버 에러 코드에 대한 처리 Interceptor 에서 걸러낸다.
 */
fun forbiddenInterceptor(): Interceptor {
    return Interceptor { chain: Interceptor.Chain ->

        val response: Response = chain.proceed(chain.request())
        when (response.code) {
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

            }
            NetInfo.CODE_BAD_GATE_WAY -> {
                JLogger.e("서버 에러 발생 CODE_BAD_GATE_WAY")
            }
            else -> {
            }
        }
        response
//        try{
//            response = chain.proceed(chain.request())
//            when(response.code()){
//                NetInfo.CODE_BAD_REQUEST -> {
//                    JLogger.e("서버 에러 발생 CODE_BAD_REQUEST")
//                }
//                NetInfo.CODE_FORBIDDEN -> {
//                    JLogger.e("서버 에러 발생 CODE_FORBIDDEN")
//                }
//                NetInfo.CODE_NOT_FOUND -> {
//                    JLogger.e("서버 에러 발생 CODE_NOT_FOUND")
//                }
//                NetInfo.CODE_ERROR -> {
//                    JLogger.e("서버 에러 발생 CODE_ERROR")
//                    throw Exception("서버가 닫혔습니다.")
//                }
//                NetInfo.CODE_BAD_GATE_WAY -> {
//                    JLogger.e("서버 에러 발생 CODE_BAD_GATE_WAY")
//                    throw Exception("서버가 닫혔습니다.")
//                }
//                else -> {}
//            }
//            response
//        } catch (e : Exception){
//            JLogger.d("forbiddenInterceptor Error ${e.message}")
////            throw Exception(e.message)
//            response
//        }
    }
}

/**
 * 헤더 값 세팅 하는 부분
 */
fun headerInterceptor(pref: AccountPref): Interceptor {
    return Interceptor { chain ->
        val origin: Request = chain.request()

        val request: Request = origin.newBuilder()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header(NetInfo.KEY_TYPE, NetInfo.VALUE_TYPE)
                .header(NetInfo.KEY_LOGIN, pref.getLoginKey())
                .method(origin.method, origin.body)
                .build()
        chain.proceed(request)
    }
}

/**
 * create Client.
 */
fun createHttpClient(interceptor: Interceptor): OkHttpClient {
    val retrofitLogger = RetrofitLogger().apply { level = RetrofitLogger.Level.BODY }
    return OkHttpClient.Builder().apply {
        retryOnConnectionFailure(true)
        connectTimeout(3, TimeUnit.SECONDS)
        readTimeout(5, TimeUnit.SECONDS)
        writeTimeout(5, TimeUnit.SECONDS)
        connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
        addInterceptor(forbiddenInterceptor())
        addInterceptor(interceptor)
        addInterceptor(retrofitLogger)
    }.build()
}

/**
 * Http 통신하는 Retrofit 생성.
 */
inline fun <reified T> createRetrofit(client: OkHttpClient): T {
    return Retrofit.Builder().apply {
        baseUrl(NetInfo.BASE_URL)
        client(client)
        addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(ArrayList::class.java, NonNullListDeserializer<Any>())
                        .registerTypeAdapter(String::class.java, StringTypeAdapter())
                        .disableHtmlEscaping()
                        .create()
        ))
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }.build().create(T::class.java)
}

/**
 * Gson ArrayList Null 인경우 NonNull 처리.
 */
class NonNullListDeserializer<T> : JsonDeserializer<ArrayList<T>> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ArrayList<T> {
        if (json is JsonArray) {
            val size = json.size()
            if (size == 0) {
                return arrayListOf()
            }
            val list: ArrayList<T> = ArrayList(size)
            for (i in 0 until size) {
                val elementType: Type = `$Gson$Types`.getCollectionElementType(typeOfT, ArrayList::class.java)
                val value: T = context.deserialize(json[i], elementType)
                if (value != null) {
                    list.add(value)
                }
            }
            return list
        }
        return arrayListOf()
    }
}

/**
 * Gson String Variable Null 값 인경우 빈값으로 처리.
 */
class StringTypeAdapter : TypeAdapter<String>() {

    override fun write(out: JsonWriter, value: String?) {
    }

    override fun read(reader: JsonReader): String {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return ""
        }
        return reader.nextString()
    }
}




