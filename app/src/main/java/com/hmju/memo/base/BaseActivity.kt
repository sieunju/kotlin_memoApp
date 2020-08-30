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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
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
    }

    protected fun setWindowFlag(bits: Int, on : Boolean) {
        val win = window ?: return
        val winParams = win.attributes
        if(on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun onRestart() {
        super.onRestart()
        JLogger.d("onRestart")
    }

    override fun onStop() {
        super.onStop()
        JLogger.d("onStop")
    }
}