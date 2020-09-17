package com.hmju.memo.base

import com.google.gson.annotations.SerializedName

/**
 * Description: BaseResponse Data Class
 *
 * Created by juhongmin on 2020/06/05
 */
open class BaseResponse(
    @SerializedName("status") val code: Boolean,
    @SerializedName("errMsg") val errMsg : String? = null
)