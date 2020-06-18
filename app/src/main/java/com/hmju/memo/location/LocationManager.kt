package com.hmju.memo.location

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.hmju.memo.utils.JLogger
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Description : Location Manager Class.
 *
 * Created by hmju on 2020-06-17
 */
class LocationManager(private val applicationContext: Context) : LifecycleObserver {

    private var mListener: Listener? = null

    fun setListener(listener: Listener) {
        mListener = listener
    }

    fun start(context: Context?) {
        JLogger.d("LocationManager 주소값 ${this.toString()}")
        context?.let {
            (it as LifecycleOwner).lifecycle.removeObserver(this)
            (it as LifecycleOwner).lifecycle.addObserver(this)
            // 권한 체크.
            with(RxPermissions(it as FragmentActivity)) {
                // android Q 버전 Background Location 처리
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    request(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        ACCESS_BACKGROUND_LOCATION
                    )
                        .subscribe { isGranted ->
                            if (isGranted) {
                                doLocation()
                            } else {
                                mListener?.onPermissionDenied()
                                return@subscribe
                            }
                        }
                } else {
                    request(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                        .subscribe { isGranted ->
                            if (isGranted) {
                                doLocation()
                            } else {
                                mListener?.onPermissionDenied()
                                return@subscribe
                            }
                        }
                }
            }
        } ?: doLocation()
    }

    private fun doLocation() {
        with(applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager) {
            JLogger.d("TEST:: doLocation")
            val isGpsEnabled = isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGpsEnabled) {

                // 마지막으로 위치 권한 체크
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        10.toFloat(),
                        gpsListener
                    )
                }
            }

            if (isNetworkEnabled) {
                requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    10.toFloat(),
                    networkListener
                )
            }

            // GPS 비활성화 상태
            if (!isGpsEnabled) {
                mListener?.onGpsAndNetworkError(true)
            }

            // 네트워크 비활성화 상태
            if (!isNetworkEnabled) {
                mListener?.onGpsAndNetworkError(false)
            }

        }
    }

    private fun stopLocation() {
        with(applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager) {
            removeUpdates(networkListener)
            removeUpdates(gpsListener)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onLocationResume() {
        JLogger.d("onResume 입니다.")
//        doLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onLocationStop() {
        JLogger.d("onStop 입니다.")
        stopLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onLocationDestroy() {
        JLogger.d("파괴파괴!!!")
//        stopLocation()
    }

    fun getLastLocation(): Location? =
        (applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager).run {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                var bestLocation: Location? = null
                getProviders(true).forEach loop@{ provider ->
                    JLogger.d("위치 타입\t$provider")
                    val tmpLocation = getLastKnownLocation(provider)
                    if(tmpLocation == null){
                        JLogger.d("위치가 널널\t$provider")
                        return@loop
                    }

                    if (bestLocation == null || tmpLocation.accuracy < bestLocation!!.accuracy) {
                        bestLocation = tmpLocation
                    }
                }

                if (bestLocation != null) {
                    JLogger.d("표시할 위치 타입\t${bestLocation!!.provider}")
                }
                bestLocation
            } else {
                null
            }
        }


    /**
     * 위치 콜백 리스너
     */
    private val networkListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let {
                mListener?.onLocation(it.provider, it.accuracy, it.latitude, it.longitude)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            JLogger.d("onStatusChanged Provider\t$provider")
        }

        override fun onProviderEnabled(provider: String?) {
            JLogger.d("onProviderEnabled Provider\t$provider")
            doLocation()
        }

        override fun onProviderDisabled(provider: String?) {
            JLogger.d("onProviderDisabled Provider\t$provider")
            stopLocation()
        }
    }

    private val gpsListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let {
                mListener?.onLocation(it.provider, it.accuracy, it.latitude, it.longitude)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            JLogger.d("onStatusChanged Provider\t$provider")
        }

        override fun onProviderEnabled(provider: String?) {
            JLogger.d("onProviderEnabled Provider\t$provider")
            doLocation()
        }

        override fun onProviderDisabled(provider: String?) {
            JLogger.d("onProviderDisabled Provider\t$provider")
            stopLocation()
        }
    }

    interface Listener {
        fun onPermissionDenied()
        fun onGpsAndNetworkError(isGpsError: Boolean)
        fun onLocation(provider: String, accuracy: Float, latitude: Double, longitude: Double)
    }
}