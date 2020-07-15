package com.hmju.memo.model.form


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/21
 */
data class MemoListParam(
    var pageNo: Int,
    var selectedTag: Int? = null,
    var keyWord: String? = null
)