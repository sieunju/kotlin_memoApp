package com.hmju.memo.repository.preferences

import android.content.SharedPreferences

/**
 * Description :
 *
 * Created by hmju on 2020-06-18
 */
class LocationPref(
    pref: SharedPreferences
) : BasePref(pref) {
    val PREF_ACCURACY = "pref_accuracy"
    val PREF_LATITUDE = "pref_latitude"
    val PREF_LONGITUDE = "pref_longitude"

    fun setAccuracy(accuracy: Float) {
        setValue(PREF_ACCURACY, accuracy)
    }

    fun getAccuracy(): Float = getValue(PREF_ACCURACY, 0F)

    fun setLatitude(latitude: String) {
        setValue(PREF_LATITUDE, latitude)
    }

    fun getLatitude(): String {
        return getValue(PREF_LATITUDE, "0.0")
    }

    fun setLongitude(longitude: String) {
        setValue(PREF_LONGITUDE, longitude)
    }

    fun getLongitude(): String {
        return getValue(PREF_LONGITUDE, "0.0")
    }
}

fun getLatitude(latitude: String): Double {
    return latitude.toDouble()
}