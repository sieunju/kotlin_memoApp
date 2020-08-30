package com.hmju.memo.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.RequestCode
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.utils.startAct
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Description : 메인 페이지
 *
 * Created by hmju on 2020-05-10
 */
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
            })

            finish.observe(this@MainActivity, Observer {
                // 앱 종료.
                if (it) {
                    ActivityCompat.finishAffinity(this@MainActivity)
                } else {
                    // SnackBar 노출.
                    Snackbar.make(clRoot, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
                }
            })

            setupInsets()
            start()
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
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

    private fun setupInsets() {
        val baseMoviesPadding = pxFromDp(5f)
        val toolbarHeight = clHeaderToolbar.layoutParams.height

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            clRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            clRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            clHeaderToolbar.updatePadding(top = toolbarHeight + baseMoviesPadding)
        }

        clRoot.requestLayout()

//        ViewCompat.setOnApplyWindowInsetsListener(toolBarHeader) { view, insets ->
////            toolBarHeader.setMarginTop(insets.systemWindowInsetTop)
////            toolBarHeader.setMarginTop(0)
//            toolBarHeader.post(Runnable {
//                nsContents.updatePadding(
//                    top = toolBarHeader.height
//                            + baseMoviesPadding
//                )
//
////                nsContents.updatePadding(
////                    top = toolBarHeader.height
////                            + insets.systemWindowInsetTop
////                            + baseMoviesPadding
////                )
//            })
//
//            insets
//        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(nsContents) { view, insets ->
//            nsContents.updatePadding(bottom = insets.systemWindowInsetBottom)
//            insets
//        }
//        clRoot.requestLayout()
    }

    private fun View.setMarginTop(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = value
    }

    private fun pxFromDp(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
