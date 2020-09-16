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
    @SerializedName("fileList") val fileList: ArrayList<FileItem>? = null
) : Serializable {

    val isNormal: Boolean
        get() = fileList.isNullOrEmpty()

    fun thumbImg(): String? {
        return if (fileList.isNullOrEmpty()) {
            ""
        } else {
            fileList[0].filePath
        }
    }
}

// Memo File Item
data class FileItem(
    @SerializedName("manageNo") val manageNo: Int,
    @SerializedName("path") val filePath: String? = ""
) : Serializable

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