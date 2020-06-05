package com.hmju.memo.repository.preferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/30
 */
interface BasePrefImpl {
    fun <T> setValue(key: String, value: T)
    fun getValue(key: String, defaultValue: String) : String
    fun getValue(key: String,defaultValue: Int) : Int
    fun getValue(key: String,defaultValue: Boolean) : Boolean
    fun getValue(key: String, defaultValue: Long) : Long
}