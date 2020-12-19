package com.hmju.memo.model.login

import com.google.gson.annotations.SerializedName
import com.hmju.memo.base.BaseResponse

/**
 * Description: 로그인 Response Data Class
 *
 * Created by juhongmin on 2020/06/05
 */
data class LoginResponse(
    @SerializedName("user_nm") val userName: String? = null,
    @SerializedName("res_path") val resPath: String? = null,
    @SerializedName("login_key") val loginKey: String? = null
) : BaseResponse()