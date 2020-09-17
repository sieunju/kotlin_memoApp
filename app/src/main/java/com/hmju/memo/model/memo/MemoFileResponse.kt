package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

data class MemoFileResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("errMsg") val errMsg: String? = null,
    @SerializedName("msg") val successMsg: String? = null,
    @SerializedName("patList") val pathList: ArrayList<String>? = null
)