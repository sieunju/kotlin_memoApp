package com.hmju.memo.di

import com.hmju.memo.location.LocationListener
import com.hmju.memo.location.LocationManager
import com.hmju.memo.location.LocationManagerImpl
import org.koin.dsl.module

/**
 * Description :
 *
 * Created by hmju on 2020-06-17
 */

val locationModule = module {

    single { LocationManager(get()) }
}