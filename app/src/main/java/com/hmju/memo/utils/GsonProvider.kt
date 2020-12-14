package com.hmju.memo.utils

import android.content.Context
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

/**
 * Description : Gson Util
 *
 * Created by juhongmin on 2020/12/06
 */

interface GsonProvider {
    val gson: Gson
    fun <T : Any> getValue(jsonObject: JsonElement, key: String, type: Class<T>): T?
    fun <T : Any> getList(jsonObject: JsonObject, key: String, type: Class<T>): ArrayList<T>?
    fun <T : Any> getObject(jsonObject: JsonObject, type: Class<T>): T?
    fun <T : Any> getObject(jsonElement: JsonElement, type: Class<T>): T?
}

class GsonProviderImpl(private val ctx: Context) : GsonProvider {
    override val gson: Gson = GsonBuilder().create()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(jsonObject: JsonElement, key: String, type: Class<T>): T? {
        try {
            when (type) {
                String::class.java -> {
                    return jsonObject.asJsonObject[key].asString as T
                }
                Int::class.java -> {
                    return jsonObject.asJsonObject[key].asInt as T
                }
                Double::class.java -> {
                    return jsonObject.asJsonObject[key].asDouble as T
                }
                Boolean::class.java -> {
                    return jsonObject.asJsonObject[key].asBoolean as T
                }
                else -> {
                    return null
                }
            }
        } catch (ex: Exception) {
            JLogger.d("getValue Error ${ex.message}")
            return null
        }
    }

    override fun <T : Any> getList(
        jsonObject: JsonObject,
        key: String,
        type: Class<T>
    ): ArrayList<T>? {
        return try {
            val result: JsonArray = jsonObject.getAsJsonArray(key)
            gson.fromJson(
                result.toString(),
                TypeToken.getParameterized(ArrayList::class.java, type).type
            )
        } catch (ex: Exception) {
            JLogger.d("Error?? ${ex.message}")
            null
        }
    }

    override fun <T : Any> getObject(jsonObject: JsonObject, type: Class<T>): T? {
        try {
            return gson.fromJson(jsonObject.toString(), type)
        } catch (ex: Exception) {
            JLogger.d("Error ? ${ex.message}")
            return null
        }
    }

    override fun <T : Any> getObject(jsonElement: JsonElement, type: Class<T>): T? {
        try {
            return gson.fromJson(jsonElement, type)
        } catch (ex: Exception) {
            JLogger.d("Error? ${ex.message}")
            return null
        }
    }
}