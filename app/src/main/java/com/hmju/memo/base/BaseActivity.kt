package com.hmju.memo.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.hmju.memo.utils.JLogger

/**
 * Description: BaseActivity Class
 *
 * Created by juhongmin on 2020/06/04
 */
abstract class BaseActivity<T : ViewDataBinding, VM : BaseViewModel>
    : AppCompatActivity() {

    private lateinit var binding: T

    abstract val layoutId: Int
    abstract val viewModel: VM
    abstract val bindingVariable: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(bindingVariable, viewModel)
    }

    protected fun fullScreen() {
        // 지원되는 버전 만 StatusBar 반투명하게.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                statusBarColor = Color.TRANSPARENT

                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }
}