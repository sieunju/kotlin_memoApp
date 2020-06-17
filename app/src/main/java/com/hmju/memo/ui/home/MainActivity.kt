package com.hmju.memo.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.RequestCode
import com.hmju.memo.dialog.ConfirmDialog
import com.hmju.memo.location.LocationListener
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.JLogger
import com.hmju.memo.location.LocationManager
import com.hmju.memo.utils.startActResult
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

            startLogin.observe(this@MainActivity, Observer {
                startActResult<LoginActivity>(RequestCode.LOGIN) {}
            })

            startAlert.observe(this@MainActivity, Observer {
                ConfirmDialog(
                    this@MainActivity,
                    "안녕하세요 테스트입니다."
                )
            })

            startTest.observe(this@MainActivity, Observer {
                checkPermission()
            })

            start()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    with(viewModel) {
                        start()
                    }
                }
            }
        }
    }

    private fun checkPermission() {
//        with(RxPermissions(this)){
//            requestEach(
//                Manifest.permission.BLUETOOTH,
//                Manifest.permission.BLUETOOTH_ADMIN,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            ).subscribe{permission->
//                if(permission.granted){
//                    JLogger.d("권한 승인 ${permission.name}")
//                } else {
//                    JLogger.d("권한 취소 ${permission.name}")
//                }
//            }
//        }
//        with(RxPermissions(this)){
//            request(
//                Manifest.permission.BLUETOOTH,
//                Manifest.permission.BLUETOOTH_ADMIN,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            ).subscribe{ allGranted->
//                if(allGranted){
//                    JLogger.d("전부다 승인")
//                } else {
//                    JLogger.d("승인 ㄴㄴ")
//                }
//            }
//        }

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
