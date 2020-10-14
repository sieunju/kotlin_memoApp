package com.hmju.memo

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.IntentCompat
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import com.hmju.memo.di.apiModule
import com.hmju.memo.di.appModule
import com.hmju.memo.di.prefModule
import com.hmju.memo.di.viewModelModule
import com.hmju.memo.ui.home.MainActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketException
import kotlin.system.exitProcess

/**
 * Description: Main Application Class
 *
 * Created by juhongmin on 2020/05/30
 */
class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Bean -> 싱글톤
            // Factory -> 매번 인스턴스 생성.
            androidContext(this@MainApplication)
            modules(
                prefModule +
                        appModule +
                        viewModelModule +
                        apiModule
            )
        }

        // 테스트 코드 추가.
//        val pref : AccountPref by inject()
//        pref.setLoginKey("")

        initRxJava()

        setTheme()

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        registerComponentCallbacks(componentCallbacks)
    }

    /**
     * reactivex.exceptions.UndeliverableException 처리 함수.
     */
    private fun initRxJava() {
        // reactivex.exceptions.UndeliverableException
        // 참고 링크 https://thdev.tech/android/2019/03/04/RxJava2-Error-handling/
        RxJavaPlugins.setErrorHandler { e ->
            var error = e
            if (error is UndeliverableException) {
                error = e.cause
            }
            if (error is IOException || error is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                JLogger.e("RxJava IOException Or SocketException ${error.message}")
                return@setErrorHandler
            }
            if (error is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                JLogger.e("RxJava InterruptedException ${error.message}")
                return@setErrorHandler
            }
            if (error is NullPointerException || error is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(),
                    error
                )
                JLogger.e("RxJava NullPointerException Or IllegalArgumentException ${error.message}")
                return@setErrorHandler
            }
            if (error is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(),
                    error
                )
                JLogger.e("RxJava IllegalStateException ${error.message}")
                return@setErrorHandler
            }
        }
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        var currentActivity: WeakReference<Activity>? = null
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//            JLogger.d("onActivityCreated $activity")
            currentActivity?.clear()
            currentActivity = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
//            JLogger.d("onActivityDestroyed $activity")
        }
    }

    private val componentCallbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
//            JLogger.d("onConfigurationChanged $newConfig")
            // 화면 스타일 변경시 앱 재시작.
            activityLifecycleCallbacks.currentActivity?.get()?.let{
                it.showToast(R.string.str_ui_mode_changed_info, Toast.LENGTH_LONG)
                it.applicationRestart()
            }
        }

        override fun onLowMemory() {
//            JLogger.d("onLowMemory")
        }

        override fun onTrimMemory(level: Int) {
//            JLogger.d("onTrimMemory $level")
        }
    }

    private fun setTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
    }

    fun Activity.applicationRestart() {
        finishAffinity()
        val intent = Intent(this@MainApplication,MainActivity::class.java)
        startActivity(intent)
        exitProcess(0)
    }
}