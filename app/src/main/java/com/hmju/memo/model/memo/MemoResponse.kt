package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName
import com.hmju.memo.utils.JLogger

// Memo Data Response.
data class MemoResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("dataList") val dataList: ArrayList<MemoItem>,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

// Memo Item
data class MemoItem(
    @SerializedName("TAG") val tag: Int,
    @SerializedName("MEMO_ID") val manageNo: Int,
    @SerializedName("TITLE") val title: String? = "",
    @SerializedName("CONTENTS") val contents: String? = "",
    @SerializedName("IMAGES") val images: String? = null
) {

    init {
        JLogger.d("TTTT??!@12")
    }

    fun isNormal() : Boolean {
        return images.isNullOrEmpty()
    }

    fun thumbImg(): String? {
        JLogger.d("Images String?? " + images);
        return "";
    }
}