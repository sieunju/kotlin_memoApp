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
    const val MEMO_DETAIL_DELETE = "c"
    const val MEMO_DETAIL_MANAGE_NO = "d"
    const val GALLERY_IMG_LIMIT = "a"
    const val GALLERY_SELECT_IMAGES = "a"
    const val CAMERA_CAPTURE_PHOTO_URI = "a"
}

object RequestCode {
    const val LOGIN = 100
    const val MEMO_DETAIL = 200
    const val MEMO_ADD = 201
    const val GALLERY = 300
    const val CAMERA_CAPTURE = 400
}

object ResultCode {
    const val CAMERA_CAPTURE_OK = 401
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

object Etc {
    const val IMG_MIME_TYPE_FILE_EXTENSION = "image/jpg"
    const val IMG_FILE_EXTENSION = ".jpg"
    const val IMG_FILE_LIMIT : Int = 5
}