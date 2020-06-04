package com.hmju.memo.preferences

import android.content.SharedPreferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/31
 */
abstract class BasePref(
    private val pref: SharedPreferences
) : BasePrefImpl {
    override fun <T> setValue(key: String, value: T) {
        when(value){
            is String -> {
                with(pref.edit()){
                    putString(key,value)
                    apply()
                }
            }
            is Long -> {
                with(pref.edit()){
                    putLong(key,value)
                    apply()
                }
            }
            is Int -> {
                with(pref.edit()){
                    putInt(key,value)
                    apply()
                }
            }
            is Boolean -> {
                with(pref.edit()){
                    putBoolean(key,value)
                    apply()
                }
            }
        }
    }

    override fun getValue(key: String, defaultValue: String): String {
        pref.getString(key,defaultValue)?.let{
            return it
        } ?: run {
            return ""
        }
    }

    override fun getValue(key: String, defaultValue: Int): Int {
        return pref.getInt(key,defaultValue)
    }

    override fun getValue(key: String, defaultValue: Boolean): Boolean {
        return pref.getBoolean(key,defaultValue)
    }

    override fun getValue(key: String, defaultValue: Long): Long {
        return pref.getLong(key,defaultValue)
    }
}