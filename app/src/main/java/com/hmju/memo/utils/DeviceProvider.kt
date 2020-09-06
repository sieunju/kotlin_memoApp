package com.hmju.memo.utils

import android.content.Context

/**
 * Description : Device Provider Class
 *
 * Created by juhongmin on 2020/09/05
 */

interface DeviceProvider {
    fun getContext() : Context
    fun isNavigationBar() : Boolean
}

class DeviceProviderImpl(private val ctx: Context) : DeviceProvider {
    private val res by lazy { ctx.resources }
    override fun getContext() = ctx

    override fun isNavigationBar(): Boolean {
        val id = ctx.resources.getIdentifier("config_showNavigationBar","bool","android")
        return id > 0 && res.getBoolean(id)
    }
}

