package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

data class MemoResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("errMsg") val errMsg: String? = null,
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("manageNo") val manageNo: Int
)