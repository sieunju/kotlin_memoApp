package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

data class MemoResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("errMsg") val errMsg: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("manageNo") val manageNo: Int
)