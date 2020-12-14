package com.hmju.memo.di

import com.hmju.memo.fcm.FCMProvider
import com.hmju.memo.fcm.FCMProviderImpl
import com.hmju.memo.utils.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Description : App 관련 Module
 * Resource, Device 정보
 *
 * Created by juhongmin on 2020/09/05
 */
val appModule = module {

    factory<ResourceProvider> {
        ResourceProviderImpl(androidApplication())
    }

    factory<ImageFileProvider> {
        ImageFileProviderImpl(androidContext())
    }

    factory<DeviceProvider> {
        DeviceProviderImpl(androidContext())
    }

    factory<CursorProvider> {
        CursorProviderImpl(androidContext())
    }

    factory<FCMProvider> {
        FCMProviderImpl(androidContext())
    }

    factory<GsonProvider> {
        GsonProviderImpl(androidContext())
    }
}