package com.hmju.memo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver

/**
 * Description : BaseFragment Class
 *
 * Created by juhongmin on 2020/06/08
 */
abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>
    : Fragment(), LifecycleObserver {

    private var _binding: B? = null
    val binding get() = _binding

    abstract val layoutId: Int
    abstract val viewModel: VM
    abstract val bindingVariable: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<B>(
            inflater,
            layoutId,
            container,
            false
        ).apply {
            lifecycleOwner = this@BaseFragment
            setVariable(bindingVariable, viewModel)
            _binding = this
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Binding Memory Leak 방어 코드.
        _binding = null
    }
}