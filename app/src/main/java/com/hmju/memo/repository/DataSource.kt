package com.hmju.memo.repository

import com.hmju.memo.base.BaseResponse
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.network.paging.PagingModel
import io.reactivex.Single

/**
 * Description : 상황에 맞게 Local or Remote 처리하는 DataSource.
 *
 * Created by hmju on 2020-12-21
 */
interface DataSource {
    fun postMemo(form: MemoItemForm): Single<MemoResponse>
    fun deleteMemo(manageNo: Int): Single<BaseResponse>
    fun postMemoImages(manageNo: Int, pathList: List<String>): Single<MemoFileResponse>
    fun deleteMemoImages(files: List<FileItem>): Single<BaseResponse>
    fun fetchMemoList(params: MemoListParam): PagingModel<MemoItem>
}