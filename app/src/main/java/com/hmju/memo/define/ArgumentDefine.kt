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
    const val MEMO_DETAIL_ADD_ENTER = "e"
    const val GALLERY_IMG_LIMIT = "a"
    const val GALLERY_SELECT_IMAGES = "a"
    const val CAMERA_CAPTURE_PHOTO_URI = "a"
    const val IMAGE_EDIT_PHOTO_URIS = "a"
    const val IMAGE_DETAIL_POS = "a"
    const val IMAGE_DETAIL_PATH_LIST = "b"
    const val PUSH_LINK_URL = "push_link_url"
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
    const val DEFAULT_GALLERY_FILTER_ID = "ALL"
    const val DEFAULT_GALLERY_FILTER_NAME = "최근 항목"
}

object TestCardType {
    const val KEY_CARD = "v_card_type"
    const val KEY_SUB = "v_sub_card_type"

    const val A0001 = "A0001"
    const val A0002 = "A0002"
    const val A0003 = "A0003"
    const val A0004 = "A0004"
    const val A0005 = "A0005"
    const val B0001 = "B0001"
    const val B0002 = "B0002"
    const val B0003 = "B0003"
}