package com.hmju.memo.location

/**
 * Description :
 *
 * Created by hmju on 2020-06-17
 */
interface LocationListener {
    fun onPermissionDenied()
    fun onGpsAndNetworkError(isGpsError: Boolean)
    fun onLocation(provider: String, accuracy: Float, latitude: Double, longitude: Double)
}