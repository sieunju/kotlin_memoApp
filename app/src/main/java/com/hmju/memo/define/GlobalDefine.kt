package com.hmju.memo.define

import androidx.annotation.ColorRes
import com.hmju.memo.R

/**
 * Description :
 *
 * Created by hmju on 2020-06-12
 */

object ExtraCode {
    const val MEMO_MANAGE_NO = "memo_manage_no"
    const val MEMO_DETAIL = "a"
    const val MEMO_DETAIL_POS = "b"
    const val ALBUM_MANAGE_NO = "a"
}

object RequestCode {
    const val LOGIN = 100
    const val MEMO_DETAIL = 200
    const val ALBUM = 300
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

enum class TagType(val tag: Int, @ColorRes val color: Int) {
    RED(1, R.color.color_tag1),
    ORANGE(2, R.color.color_tag2),
    YELLOW(3, R.color.color_tag3),
    GREEN(4, R.color.color_tag4),
    BLUE(5, R.color.color_tag5),
    PURPLE(6, R.color.color_tag6),
    ETC(7, R.color.color_tag7)
}