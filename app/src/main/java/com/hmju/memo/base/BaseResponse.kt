package com.hmju.memo.base

import com.google.gson.annotations.SerializedName

/**
 * Description: BaseResponse Data Class
 *
 * Created by juhongmin on 2020/06/05
 */
open class BaseResponse {
    @SerializedName("status")
    var code: Boolean? = false

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("errMsg")
    var errMsg: String? = null
}