package com.hmju.memo.model.form

import com.google.gson.annotations.SerializedName
import com.hmju.memo.model.memo.FileItem

/**
 * Description :
 *
 * Created by juhongmin on 2020/10/03
 */
data class DeleteMemoItem(
    @SerializedName("memo_id") val mangeNo: Int? = -1,
    @SerializedName("fileList") val pathList: List<FileItem>? = null
)