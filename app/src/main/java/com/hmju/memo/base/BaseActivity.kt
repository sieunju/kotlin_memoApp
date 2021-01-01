package com.hmju.memo.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.hmju.memo.R
import com.hmju.memo.dialog.LoadingDialog

/**
 * Description: BaseActivity Class
 *
 * Created by juhongmin on 2020/06/04
 */
abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>
    : AppCompatActivity(), LifecycleObserver {

    private var _binding: B? = null
    val binding get() = _binding!!

    abstract val layoutId: Int
    abstract val viewModel: VM
    abstract val bindingVariable: Int

    companion object {
        const val TRANSITIONNAME = "transName"
    }

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<B>(this, layoutId).apply {
            lifecycleOwner = this@BaseActivity
            setVariable(bindingVariable, viewModel)
            _binding = this
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Binding Memory Leak 방어 코드.
        _binding = null
    }

    protected fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window ?: return
        val winParams = win.attributes
        if (on) {
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

    protected fun setFitsWindowsKeyboard() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }

    /**
     * Material Design Activity Animation Func..
     */
    protected fun onTransFormationStartContainer() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    /**
     * Material Design Activity Animation Func..
     */
    protected fun onTransFormationEndContainer() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        ViewCompat.setTransitionName(findViewById(android.R.id.content), TRANSITIONNAME)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = customMaterialContainerTransform()
        window.sharedElementReturnTransition = customMaterialContainerTransform()
    }

    /**
     * Material Design Activity Animation Func..
     */
    private fun customMaterialContainerTransform(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500
            pathMotion = MaterialArcMotion()
            drawingViewId = android.R.id.content
            containerColor = ContextCompat.getColor(this@BaseActivity, R.color.colorBackground)
            scrimColor = ContextCompat.getColor(this@BaseActivity, R.color.colorBackground)
            transitionDirection = MaterialContainerTransform.TRANSITION_DIRECTION_AUTO
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            fitMode = MaterialContainerTransform.FIT_MODE_AUTO
        }
    }

    /**
     * 로딩 화면 노출
     */
    fun showLoadingDialog() {
        if (isFinishing) {
            return
        }

        loadingDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        } ?: run {
            loadingDialog = LoadingDialog(this)
            loadingDialog!!.show()
        }
    }

    /**
     * 로딩 화면 숨김.
     */
    fun dismissLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}