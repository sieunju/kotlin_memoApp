package com.hmju.memo.model.login

import com.google.gson.annotations.SerializedName
import com.hmju.memo.base.BaseResponse

/**
 * Description: 로그인 Response Data Class
 *
 * Created by juhongmin on 2020/06/05
 */
data class LoginResponse(
    @SerializedName("resCode") val code: Int,
    @SerializedName("resMsg") val msg: String,
    @SerializedName("loginKey") val loginKey: String?
)