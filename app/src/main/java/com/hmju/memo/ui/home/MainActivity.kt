package com.hmju.memo.ui.home

import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.location.LocationManager
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()

    override val bindingVariable = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {

            startPermission.observe(this@MainActivity, Observer {
                checkPermission()
            })

        }
    }

    private fun checkPermission() {

        val manager: LocationManager by inject()
        manager.start(this)
        manager.setListener(object : LocationManager.Listener {
            override fun onPermissionDenied() {
                ConfirmDialog(this@MainActivity, "권한이 필요합니다.")
            }

            override fun onGpsAndNetworkError(isGpsError: Boolean) {
                if (isGpsError) {
                    ConfirmDialog(this@MainActivity, "GPS 활성화 해주세요")
                } else {
                    ConfirmDialog(this@MainActivity, "네트워크 활성화 해주세요.")
                }
            }

            override fun onLocation(
                provider: String,
                accuracy: Float,
                latitude: Double,
                longitude: Double
            ) {
                if (provider.equals(android.location.LocationManager.GPS_PROVIDER, true)) {
                    JLogger.d("==================[s] Gps Location Changed===================")
                    JLogger.d("정확도\t$accuracy\n위도\t$latitude\n경도$longitude")
                    JLogger.d("==================[e] Gps Location Changed===================")
                } else {
                    JLogger.d("==================[s] NetWork Location Changed===================")
                    JLogger.d("정확도\t$accuracy\n위도\t$latitude\n경도$longitude")
                    JLogger.d("==================[e] NetWork Location Changed===================")
                }
            }
        })

    }
}
