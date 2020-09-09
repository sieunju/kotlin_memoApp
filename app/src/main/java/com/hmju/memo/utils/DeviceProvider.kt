package com.hmju.memo.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.pm.PackageInfoCompat

/**
 * Description : Device Provider Class
 *
 * Created by juhongmin on 2020/09/05
 */

interface DeviceProvider {
    fun getContext(): Context
    fun isNavigationBar(): Boolean
    fun getVersionCode(): Int
    fun getDeviceWidth(): Int
    fun getDeviceHeight(): Int
    fun getStatusBarHeight(): Int
}

@Suppress("DEPRECATION")
class DeviceProviderImpl(private val ctx: Context) : DeviceProvider {
    private val res by lazy { ctx.resources }
    override fun getContext() = ctx

    override fun isNavigationBar(): Boolean {
        val id = ctx.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && res.getBoolean(id)
    }

    override fun getVersionCode(): Int {
        val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
        } else {
            packageInfo.versionCode
        }
    }

    override fun getDeviceWidth(): Int {
        return res.displayMetrics.widthPixels
    }

    override fun getDeviceHeight(): Int {
        return res.displayMetrics.heightPixels
    }

    override fun getStatusBarHeight(): Int {
        val id = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (id > 0) res.getDimensionPixelSize(id) else -1
    }
}

