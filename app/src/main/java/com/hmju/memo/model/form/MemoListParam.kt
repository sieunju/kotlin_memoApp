package com.hmju.memo.model.form


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
data class MemoListParam(
    var pageNo: Int,
    var selectTag: Int? = 0,
    var keyword: String? = ""
)