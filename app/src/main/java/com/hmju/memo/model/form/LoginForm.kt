package com.hmju.memo.model.form

import com.google.gson.annotations.SerializedName

/**
 * Description: 로그인 Form Data Class
 *
 * Created by juhongmin on 2020/06/05
 */

data class LoginForm(
    @SerializedName("user_id") val id :String? = "",
    @SerializedName("user_pw") val pw : String? = ""
)