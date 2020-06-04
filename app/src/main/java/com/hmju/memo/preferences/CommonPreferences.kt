package com.hmju.memo.preferences

import android.content.SharedPreferences

/**
 * Description: Preference Class
 *
 * Created by juhongmin on 2020/05/30
 */
class CommonPreferences(private val prefs: SharedPreferences) {

    companion object{
        val PREF_LOGIN_KEY = "login_key"
    }
}