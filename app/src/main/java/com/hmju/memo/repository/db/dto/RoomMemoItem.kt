package com.hmju.memo.repository.db.dto

/**
 * Description :
 *
 * Created by hmju on 2020-12-22
 */
data class RoomMemoItem(
    val manageNo: Int,
    val TAG: Int,
    val TITLE: String,
    val CONTENTS: String,
    val imgNo: Int,
    val IMG_PATH: String? = null,
    val REGISTER_DATE: Long? = null
)