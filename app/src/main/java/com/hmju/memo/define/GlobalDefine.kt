package com.hmju.memo.define

/**
 * Description :
 *
 * Created by hmju on 2020-06-12
 */

object ExtraCode {
    const val MEMO_MANAGE_NO = "memo_manage_no"
    const val MEMO_DETAIL = "memo_detail"
}

object RequestCode {
    const val LOGIN = 100
    const val MEMO_DETAIL = 200
}

object ResultCode {

}

object ToolBarDefine {
    const val POS_HOME = 3
    const val POS_ADD = 2
    const val POS_SEARCH = 1
    const val POS_MORE = 0
}

enum class NetworkState {
    SUCCESS,
    LOADING,
    ERROR,
    RESULT_EMPTY
}