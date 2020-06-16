package com.hmju.memo.model.memo

import java.io.Serializable

/**
 * Description : 메모 자세히 보기 구조체.
 *
 * Created by hmju on 2020-06-16
 */
data class MemoDetailInfo(
    val manageNo : Int,
    val tag : Int,
    val title : String?,
    val contents : String?,
    val imageList: ArrayList<String>?
) : Serializable