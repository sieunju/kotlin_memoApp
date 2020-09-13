package com.hmju.memo.model.memo

import android.view.View
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Memo Data Response.
data class MemoResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("dataList") val dataList: ArrayList<MemoItem>,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

// Memo Item
@SuppressWarnings("serial")
data class MemoItem(
    @SerializedName("manageNo") val manageNo: Int,
    @SerializedName("tag") var tag: Int,
    @SerializedName("title") var title: String? = "",
    @SerializedName("contents") var contents: String? = "",
    @SerializedName("fileList") val imgList: ArrayList<String>? = null
) : Serializable {

    val isNormal: Boolean
        get() = imgList.isNullOrEmpty()

    fun thumbImg(): String? {
        return if (imgList.isNullOrEmpty()) {
            ""
        } else {
            imgList[0]
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

data class MemoItemAndView(
    val view: View,
    val item: MemoItem
)