package com.hmju.memo.convenience

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Description : Network State Live Data.
 *
 * Created by juhongmin on 1/3/21
 */
class ConnectionLiveData : LiveData<Boolean>(), KoinComponent {
    private val context: Context by inject()
    private var manager: ConnectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var connectivityCallback: ConnectivityManager.NetworkCallback

    private val builder: NetworkRequest.Builder = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onActive() {
        super.onActive()
        postValue(isNetworkAvailable())
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> manager.registerDefaultNetworkCallback(connectivityCallbackApi23())
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> networkAvailableRequestApi23()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> networkAvailableRequestApi21()
            else -> {
                @Suppress("DEPRECATION")
                context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.unregisterNetworkCallback(connectivityCallback)
        } else {
            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun networkAvailableRequestApi21() {
        manager.registerNetworkCallback(builder.build(), connectivityCallbackApi21())
    }

    private fun networkAvailableRequestApi23() {
        manager.registerNetworkCallback(builder.build(), connectivityCallbackApi23())
    }

    private fun connectivityCallbackApi21(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    postValue(true)
                }

                override fun onLost(network: Network) {
                    postValue(false)
                }
            }
            return connectivityCallback
        } else {
            throw IllegalAccessError("Accessing wrong API version")
        }
    }

    private fun connectivityCallbackApi23(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        postValue(true)
                    }
                }

                override fun onLost(network: Network) {
                    postValue(false)
                }
            }
            return connectivityCallback
        } else {
            throw IllegalAccessError("Accessing wrong API version")
        }
    }


    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            postValue(isNetworkAvailable())
        }
    }

    /**
     * Network or Cellular State
     */
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = manager.activeNetwork ?: return false
            val actNw = manager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return manager.activeNetworkInfo?.isConnected ?: false
        }
    }

}