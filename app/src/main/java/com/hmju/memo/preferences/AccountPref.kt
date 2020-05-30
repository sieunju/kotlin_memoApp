package com.hmju.memo.preferences

import android.content.SharedPreferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/30
 */
class AccountPref(
    private val pref: SharedPreferences)
    : BasePref{

    val PREF_LOGIN_KEY = "login_key"

    fun setLoginKey(loginKey: String){

    }

    override fun setValue(key: String, value: String) {
        pref.edit().putString(key,value).apply()
    }

    override fun setValue(key: String, value: Int) {
        pref.edit().putInt(key,value).apply()
    }

    override fun setValue(key: String, value: Boolean) {
        pref.edit().putBoolean(key,value).apply()
    }

    override fun <T> setValue(key: String, value: T) {
        when(value){
            is String -> {
                with(pref.edit()){
                    putString(key,value)
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
                }
            }
        }
    }

    override fun <T> getValue(key: String, defaultValue: String): T {
        TODO("Not yet implemented")
    }

    override fun <T> getValue(key: String, defaultValue: Int): T {
        TODO("Not yet implemented")
    }

    override fun <T> getValue(key: String, defaultValue: Boolean): T {
        TODO("Not yet implemented")
    }
}