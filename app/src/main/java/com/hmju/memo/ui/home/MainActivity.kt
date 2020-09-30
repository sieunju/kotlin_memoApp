package com.hmju.memo.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ToolBarDefine.POS_HOME
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveMemoDetail
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
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

            startMemoDetail.observe(this@MainActivity, Observer { triple ->
                moveMemoDetail(triple)
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
                if (resultCode == RESULT_OK) {
                    with(viewModel) {
                        start()
                    }
                }
            }
            RequestCode.MEMO_DETAIL -> {
                if (resultCode == RESULT_OK) {
                    // 메모 상세에서 데이터가 변경 했다면 데이터 갱신 처리.
                    data?.let {
                        val clickPos = it.getIntExtra(ExtraCode.MEMO_DETAIL_POS, -1)
                        if (clickPos != -1) {
                            val changedItem =
                                it.getSerializableExtra(ExtraCode.MEMO_DETAIL) as MemoItem
                            rvContents.adapter?.let { adapter ->
                                if (adapter is MemoListAdapter) {
                                    adapter.setChangedData(clickPos, changedItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
