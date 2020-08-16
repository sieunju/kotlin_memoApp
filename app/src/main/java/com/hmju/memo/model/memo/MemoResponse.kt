package com.hmju.memo.model.memo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.hmju.memo.utils.JLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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

    var imgList: ArrayList<String>? = null

    fun isNormal(): Boolean {
        return images.isNullOrEmpty()
    }

    fun thumbImg(): String? {
        imgList?.let{
            return it[0]
        } ?: return ""
    }

    fun bindingImgList() {
        try {
            images?.let { imgs ->
                imgList = GsonBuilder().create().fromJson(imgs, ArrayList<String>()::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun bindingTest(images: String?) {
    images?.let { imgs ->
        try {
            val array = GsonBuilder().create().fromJson(imgs, ArrayList<String>()::class.java)
            println("Array ${array[0]}")
        } catch (e: Exception) {
            println(e.message)
        }

    }

}