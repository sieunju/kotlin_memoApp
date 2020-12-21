package com.hmju.memo.ui.memo

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hmju.memo.BR
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.databinding.ActivityMainBinding
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.NetworkState
import com.hmju.memo.define.RequestCode
import com.hmju.memo.define.ToolBarDefine.POS_ADD
import com.hmju.memo.define.ToolBarDefine.POS_HOME
import com.hmju.memo.define.ToolBarDefine.POS_SEARCH
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.adapter.MemoListAdapter
import com.hmju.memo.ui.login.LoginActivity
import com.hmju.memo.ui.toast.showToast
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.moveMemoDetail
import com.hmju.memo.utils.startAct
import com.hmju.memo.utils.startActResult
import com.hmju.memo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

/**
 * Description : 메인 페이지
 *
 * Created by hmju on 2020-05-10
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener, LifecycleObserver {

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

            startNetworkState.observe(this@MainActivity, Observer { state ->
                when (state) {
                    NetworkState.LOADING -> {
                        showLoadingDialog()
                    }
                    NetworkState.ERROR -> {
                        dismissLoadingDialog()
                    }
                    else -> {
                        dismissLoadingDialog()
                    }
                }
            })

            networkState.observe(this@MainActivity, Observer { state ->
                JLogger.d("PagedNetwork State $state")
                when (state) {
                    NetworkState.SUCCESS -> {
                        // 전체 로딩 Dismiss
                        dismissLoadingDialog()
                    }
                }
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
                    POS_ADD -> {
                        // 메모 추가 페이지 진입
                        startActResult<MemoDetailActivity>(RequestCode.MEMO_ADD) {
                            putExtra(ExtraCode.MEMO_DETAIL_ADD_ENTER, true)
                        }
                    }
                    POS_SEARCH -> {

                    }
                }
            })

            startMemoDetail.observe(this@MainActivity, Observer { triple ->
                moveMemoDetail(triple)
            })

            finish.observe(this@MainActivity, Observer {
                // 앱 종료.
                if (it) {
                    moveTaskToBack(true)
                    finishAndRemoveTask()
                    exitProcess(0)
                } else {
                    showToast(R.string.str_back_press_info)
                }
            })

            // ToolBar Setting
            bottomToolBar.lifeCycle = lifecycle
            refresh.setOnRefreshListener(this@MainActivity)

            // API 호출.
            start()

//            pushDeepLink(intent)
        }
    }

    private fun pushDeepLink(intent: Intent) : Boolean{
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
        return true
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
                            // 해당 메모장이 삭제 되었다면.
                            if (it.getBooleanExtra(ExtraCode.MEMO_DETAIL_DELETE, false)) {
                                // 갱신 처리.
                                onRefresh()
                            } else {
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
    } // onActivityResult

    override fun onRefresh() {
        JLogger.d("onRefresh!!")
        with(viewModel) {
            rvContents.scrollToPosition(0)
            refresh()
            refresh.isRefreshing = false
        }
    }
}
