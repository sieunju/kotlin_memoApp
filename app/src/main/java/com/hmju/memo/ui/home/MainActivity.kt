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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ToolBarDefine.POS_HOME
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.ui.memo.MemoDetailFragment
import com.hmju.memo.ui.memo.MemoFragment
import com.hmju.memo.utils.JLogger
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

            startMemo.observe(this@MainActivity, Observer {
                initMemoFragment()
            })

            startAlert.observe(this@MainActivity, Observer {
            })

            startToolBarAction.observe(this@MainActivity, Observer { toolBarPos ->
                JLogger.d("ToolBar Pos $toolBarPos")
                when (toolBarPos) {
                    POS_HOME -> {
                        // 맨위로 올리고 초기화.
                        startMemoTop.call()
                        clRoot.post {
                            clRoot.progress = 0.0F
                        }
                    }

                }
            })

            startMemoDetail.observe(this@MainActivity, Observer { item ->
                addMemoFragment(item)
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

            // StatusBar 까지 영역 표시.
            setFitsWindows()

            // API 호출.
            start()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            with(supportFragmentManager) {
                popBackStack(
                    MemoDetailFragment.TAG_DETAIL,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        } else {
            viewModel.onBackPressed()
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

    /**
     * Status Bar 영역까지 넓히는 함수.
     */
    private fun setFitsWindows() {
        val baseMoviesPadding = pxFromDp(5f)
        val toolbarHeight = clHeaderToolbar.layoutParams.height

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            clHeaderToolbar.updatePadding(top = toolbarHeight + baseMoviesPadding)
        }

        clRoot.requestLayout()
    }

    private fun pxFromDp(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setNavigationBarChanged() {

    }

    private fun initMemoFragment(isAnimate: Boolean = true) {
        with(supportFragmentManager.beginTransaction()) {
            if (isAnimate) {
                setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top)
            }
            replace(R.id.container, MemoFragment())
            commit()
        }
    }

    private fun addMemoFragment(item: MemoItem) {
        JLogger.d("자세히 보기 화면 이동!")
        with(supportFragmentManager.beginTransaction()) {
            setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top)
            replace(R.id.container, MemoDetailFragment.newInstance(item))
            addToBackStack(MemoDetailFragment.TAG_DETAIL)
            commit()
        }
    }
}
