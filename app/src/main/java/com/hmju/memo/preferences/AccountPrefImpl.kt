package com.hmju.memo.preferences

import android.content.SharedPreferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/30
 */
class AccountPrefImpl(
    private val pref: SharedPreferences)
    : BasePref(pref){

    val PREF_LOGIN_KEY = "login_key"

    fun setLoginKey(loginKey: String){
        setValue(PREF_LOGIN_KEY,loginKey)
    }
    fun getLoginKey() : String{
        return getValue(PREF_LOGIN_KEY,"")
    }
}