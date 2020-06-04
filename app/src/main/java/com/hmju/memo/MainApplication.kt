package com.hmju.memo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.hmju.memo.di.apiModule
import com.hmju.memo.di.viewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Description: Main Application Class
 *
 * Created by juhongmin on 2020/05/30
 */
class MainApplication : MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Bean -> 싱글톤
            // Factory -> 매번 인스턴스 생성.
            androidContext(this@MainApplication)
            modules(
                apiModule +
                        viewModule
            )
        }
    }
}