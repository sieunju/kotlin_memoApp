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
) : Serializable