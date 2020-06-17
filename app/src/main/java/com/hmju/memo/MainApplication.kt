package com.hmju.memo

import androidx.multidex.MultiDexApplication
import com.hmju.memo.di.apiModule
import com.hmju.memo.di.locationModule
import com.hmju.memo.di.prefModule
import com.hmju.memo.di.viewModule
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.IOException
import java.net.SocketException

/**
 * Description: Main Application Class
 *
 * Created by juhongmin on 2020/05/30
 */
class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Single -> 싱글톤
            // Factory -> 매번 인스턴스 생성.
            androidContext(this@MainApplication)
            modules(
                prefModule +
                        viewModule +
                        apiModule +
                        locationModule
            )
        }

        initRxJava()
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
                return@setErrorHandler
            }
            if (error is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (error is NullPointerException || error is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(),
                    error
                )
                return@setErrorHandler
            }
            if (error is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(),
                    error
                )
                return@setErrorHandler
            }
        }
    }
}