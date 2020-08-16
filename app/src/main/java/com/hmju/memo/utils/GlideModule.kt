package com.hmju.memo.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Description :
 *
 * Created by hmju on 2020-06-15
 */
@GlideModule
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5,TimeUnit.SECONDS)
            .connectTimeout(5,TimeUnit.SECONDS)
            .build()

        glide.registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val disCacheSizeByte = 1024 * 1024 * 100
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, disCacheSizeByte.toLong()))
        super.applyOptions(context, builder)
    }
}