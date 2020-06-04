package com.hmju.memo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

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

    private fun performDataBinding(){
        binding = DataBindingUtil.setContentView(this,layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(bindingVariable,viewModel)
        binding.executePendingBindings()
    }
}