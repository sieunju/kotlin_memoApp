package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName
import com.hmju.memo.utils.JLogger
import java.io.Serializable

/**
 * Description : Memo Item Data Class
 *
 * Created by juhongmin on 2020/10/02
 */
@SuppressWarnings("serial")
data class MemoItem(
    @SerializedName("manageNo") val manageNo: Int,
    @SerializedName("tag") var tag: Int,
    @SerializedName("title") var title: String? = "",
    @SerializedName("contents") var contents: String? = "",
    @SerializedName("fileList") var fileList: ArrayList<FileItem>? = null,
    @SerializedName("regDtm") var regDtm: String? = null
) : Serializable {

    val isNormal: Boolean
        get() = fileList.isNullOrEmpty()

    fun thumbImg(): String? {
        return if (fileList.isNullOrEmpty()) {
            ""
        } else {
            fileList!![0].filePath
        }
    }

    fun imgSize(): Int{
        return fileList?.let{
            return@let it.size
        } ?: 0
    }

    override fun equals(other: Any?): Boolean {
        return if(other is MemoItem) {
            other.manageNo == manageNo
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = manageNo
        result = 31 * result + tag
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (contents?.hashCode() ?: 0)
        result = 31 * result + (fileList?.hashCode() ?: 0)
        result = 31 * result + (regDtm?.hashCode() ?: 0)
        return result
    }
}