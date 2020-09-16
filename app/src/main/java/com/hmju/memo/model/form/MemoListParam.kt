package com.hmju.memo.model.form


/**
 * Description : MemoListParam Data Class
 * 추후 정렬순도 추가할 예정
 * Created by juhongmin on 2020/06/21
 */
data class MemoListParam(
    var pageNo: Int,
    var selectTag: Int? = 0,
    var keyword: String? = ""
)