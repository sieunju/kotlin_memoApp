package com.hmju.memo.utils

import android.content.Context
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

/**
 * Description : Gson Util Provider
 *
 * Created by juhongmin on 2020/12/06
 */
interface GsonProvider {
    fun <T : Any> getValue(jsonObject: JsonElement, key: String, type: Class<T>): T?
    fun <T : Any> getValue(json: JsonObject, key: String, type: Class<T>): T?
    fun <T : Any> getValue(json: JsonObject, key: String, defaultValue: T, type: Class<T>): T
    fun <T : Any> getList(json: JsonObject, key: String, type: Class<T>): ArrayList<T>?
    fun <T : Any> getObject(json: JsonObject, type: Class<T>): T?
    fun <T : Any> getObject(json: JsonElement, type: Class<T>): T?
}

@Suppress("UNCHECKED_CAST")
class GsonProviderImpl : GsonProvider {

    private val gson: Gson by lazy {
        GsonBuilder().create()
    }

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

    override fun <T : Any> getValue(json: JsonObject, key: String, type: Class<T>): T? {
        try {
            if (!json.has(key)) return null

            when (type) {
                String::class.java -> {
                    return json.get(key).asString as T
                }
                Int::class.java -> {
                    return json.get(key).asInt as T
                }
                Double::class.java -> {
                    return json.get(key).asDouble as T
                }
                Boolean::class.java -> {
                    return json.get(key).asBoolean as T
                }
                else -> {
                    return null
                }
            }
        } catch (ex: Exception) {
            return null
        }
    }

    override fun <T : Any> getValue(json: JsonObject, key: String, defaultValue: T, type: Class<T>): T {
        try {
            if (!json.has(key)) return defaultValue

            when (type) {
                String::class.java -> {
                    return json.get(key).asString as T
                }
                Int::class.java -> {
                    return json.get(key).asInt as T
                }
                Double::class.java -> {
                    return json.get(key).asDouble as T
                }
                Boolean::class.java -> {
                    return json.get(key).asBoolean as T
                }
                else -> {
                    return defaultValue
                }
            }

        } catch (ex: Exception) {
            return defaultValue
        }
    }

    override fun <T : Any> getList(
            json: JsonObject,
            key: String,
            type: Class<T>
    ): ArrayList<T>? {
        return try {
            val result: JsonArray = json.getAsJsonArray(key)
            gson.fromJson(
                    result.toString(),
                    TypeToken.getParameterized(ArrayList::class.java, type).type
            )
        } catch (ex: Exception) {
            JLogger.d("Error?? ${ex.message}")
            null
        }
    }

    override fun <T : Any> getObject(json: JsonObject, type: Class<T>): T? {
        return try {
            gson.fromJson(json.toString(), type)
        } catch (ex: Exception) {
            JLogger.d("Error ? ${ex.message}")
            null
        }
    }

    override fun <T : Any> getObject(json: JsonElement, type: Class<T>): T? {
        return try {
            gson.fromJson(json, type)
        } catch (ex: Exception) {
            JLogger.d("Error? ${ex.message}")
            null
        }
    }
}