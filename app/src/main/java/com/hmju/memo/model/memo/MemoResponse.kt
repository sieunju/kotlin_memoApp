package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName

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
    @SerializedName("IMAGES") val images: ArrayList<String>? = null
) {

    var isNormal = false // true 기본 형, false 이미지 형

    init {
        isNormal = images.isNullOrEmpty()
    }

    fun thumbImg(): String? {
        images?.let {
            return it[0]
        } ?: run {
            return null
        }
    }
}