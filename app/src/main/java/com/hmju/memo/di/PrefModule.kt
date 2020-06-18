package com.hmju.memo.di

import android.content.Context
import android.content.SharedPreferences
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.repository.preferences.BasePref
import com.hmju.memo.repository.preferences.BasePrefImpl
import com.hmju.memo.repository.preferences.LocationPref
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Description: Shared Preference Module Class
 *
 * Created by juhongmin on 2020/05/30
 */
val prefModule = module {
    val PREF_NAME = "memo_pref"

    factory<SharedPreferences> {
        androidContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    single<BasePrefImpl> {
        BasePref(get())
    }

    single{
        AccountPref(get())
    }

    single {
        LocationPref(get())
    }
}