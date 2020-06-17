package com.hmju.memo.location

/**
 * Description :
 *
 * Created by hmju on 2020-06-17
 */
class LocationManagerImpl : LocationListener {

    override fun onPermissionDenied() {

    }

    override fun onGpsAndNetworkError(isGpsError: Boolean) {

    }

    override fun onLocation(
        provider: String,
        accuracy: Float,
        latitude: Double,
        longitude: Double
    ) {

    }
}