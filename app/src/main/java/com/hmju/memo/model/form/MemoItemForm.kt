package com.hmju.memo.model.form

import com.google.gson.annotations.SerializedName

/**
 * @param 메모장 UID,
 * @param 메모장 우선 순위,
 * @param 메모장 제목
 * @param 메모장 내용
 */
data class MemoItemForm(
    @SerializedName("memo_id") val manageNo: Int? = null,
    @SerializedName("tag") val tag: Int,
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String
)