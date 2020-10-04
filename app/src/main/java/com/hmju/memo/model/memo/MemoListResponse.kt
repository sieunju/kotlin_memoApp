package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

data class MemoListResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("dataList") val dataList: ArrayList<MemoItem>,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)