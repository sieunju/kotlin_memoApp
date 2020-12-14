package com.hmju.memo.model.test

import com.hmju.memo.base.BaseResponse

/**
 * Description :
 *
 * Created by juhongmin on 12/13/20
 */
data class TestResponse(
    val dataList : ArrayList<TestUiModel>
)  : BaseResponse()