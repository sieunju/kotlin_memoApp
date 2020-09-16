package com.hmju.memo.ui.home

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ResultCode
import com.hmju.memo.define.ToolBarDefine.POS_HOME
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.ui.memo.MemoDetailActivity
import com.hmju.memo.ui.memo.MemoDetailFragment
import com.hmju.memo.ui.memo.MemoFragment
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveMemoDetail
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.rvContents
import kotlinx.android.synthetic.main.fragment_memo.*
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
        onTransFormationStartContainer()
        super.onCreate(savedInstanceState)

        // StatusBar 까지 영역 표시.
        setFitsWindows()

        with(viewModel) {

            startLogin.observe(this@MainActivity, Observer {
                startActResult<LoginActivity>(RequestCode.LOGIN) {}
            })

            startAlert.observe(this@MainActivity, Observer {
            })

            startToolBarAction.observe(this@MainActivity, Observer { toolBarPos ->
                JLogger.d("ToolBar Pos $toolBarPos")
                when (toolBarPos) {
                    POS_HOME -> {
                        // 맨위로 올리고 초기화.
                        rvContents.scrollToPosition(0)
                        motionRootLayout.post {
                            motionRootLayout.progress = 0.0F
                        }
                    }

                }
            })

            startMemoDetail.observe(this@MainActivity, Observer { pair ->
                moveMemoDetail(pair)
//                addMemoFragment(item)
            })

            finish.observe(this@MainActivity, Observer {
                // 앱 종료.
                if (it) {
                    ActivityCompat.finishAffinity(this@MainActivity)
                } else {
                    showToast(R.string.str_back_press_info)
                }
            })

            // API 호출.
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
