package com.hmju.memo.model.login

import com.google.gson.annotations.SerializedName

/**
 * Description: 로그인 Response Data Class
 *
 * Created by juhongmin on 2020/06/05
 */
data class LoginResponse(
    @SerializedName("status") val code: Boolean,
    @SerializedName("errMsg") val errMsg: String? = null,
    @SerializedName("loginKey") val loginKey: String?
)