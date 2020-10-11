package com.hmju.memo.di

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

    single<ResourceProvider> {
        ResourceProviderImpl(androidApplication())
    }

    single<ImageFileProvider> {
        ImageFileProviderImpl(androidContext())
    }

    single<DeviceProvider> {
        DeviceProviderImpl(androidContext())
    }
}