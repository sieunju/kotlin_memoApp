package com.hmju.memo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Description : BaseFragment Class
 *
 * Created by juhongmin on 2020/06/08
 */
abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel>
    : Fragment() {

    protected lateinit var binding: T
    abstract val layoutId: Int
    abstract val viewModel: VM
    abstract val bindingVariable: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<T>(
            inflater,
            layoutId,
            container,
            false
        ).apply {
            binding = this
            binding.lifecycleOwner = this@BaseFragment
            binding.setVariable(bindingVariable, viewModel)
        }.root
    }
}