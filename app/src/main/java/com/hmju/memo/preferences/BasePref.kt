package com.hmju.memo.preferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/30
 */
interface BasePref {
    fun setValue(key: String, value: String)
    fun setValue(key: String, value: Int)
    fun setValue(key: String, value: Boolean)
    fun <T> setValue(key: String, value: T)
    fun <T> getValue(key: String, defaultValue: String) : T
    fun <T> getValue(key: String, defaultValue: Int) : T
    fun <T> getValue(key: String, defaultValue: Boolean) : T
}