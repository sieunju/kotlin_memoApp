package com.hmju.memo.model.memo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Description : Memo File Item Data Class
 *
 * Created by juhongmin on 2020/10/02
 */
data class FileItem(
    @SerializedName("manageNo") val manageNo: Int,
    @SerializedName("path") val filePath: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return if (other is FileItem) {
            other.manageNo == manageNo
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = manageNo
        result = 31 * result + filePath.hashCode()
        return result
    }
}