package com.hmju.memo.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
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

    companion object {
        const val TRANSITIONNAME = "transName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(bindingVariable, viewModel)
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

    /**
     * Status Bar 영역까지 넓히는 함수.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun setFitsWindows() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    protected fun onTransFormationStartContainer() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    protected fun onTransFormationEndContainer() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        ViewCompat.setTransitionName(findViewById(android.R.id.content), TRANSITIONNAME)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = CustomMaterialContainerTransform()
        window.sharedElementReturnTransition = CustomMaterialContainerTransform()
    }

    internal fun CustomMaterialContainerTransform(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500
            pathMotion = MaterialArcMotion()
            drawingViewId = android.R.id.content
            containerColor = Color.WHITE
            scrimColor = Color.WHITE
            transitionDirection = MaterialContainerTransform.TRANSITION_DIRECTION_AUTO
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            fitMode = MaterialContainerTransform.FIT_MODE_AUTO
        }
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