package com.hmju.memo.repository.preferences

import android.content.SharedPreferences

/**
 * Description:
 *
 * Created by juhongmin on 2020/05/30
 */
class AccountPref(
    private val pref: SharedPreferences
) : BasePref(pref) {

    val PREF_LOGIN_KEY = "login_key"
    val PREF_FCM_TOKEN = "fcm_token"

    fun setLoginKey(loginKey: String) {
        setValue(PREF_LOGIN_KEY, loginKey)
    }

    fun getLoginKey(): String {
        return getValue(PREF_LOGIN_KEY, "")
    }

    fun setFcmToken(token: String) {
        setValue(PREF_FCM_TOKEN, token)
    }

    fun getFcmToken(): String {
        return getValue(PREF_FCM_TOKEN, "")
    }
}