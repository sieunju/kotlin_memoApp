package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

data class MemoResponse(
    @SerializedName("dataList") val dataList: ArrayList<MemoItem>,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

data class MemoItem(
    @SerializedName("TAG") val tag: Int,
    @SerializedName("MEMO_ID") val manageNo: Int,
    @SerializedName("TITLE") val title: String? = "",
    @SerializedName("CONTENTS") val contents: String? = ""
)