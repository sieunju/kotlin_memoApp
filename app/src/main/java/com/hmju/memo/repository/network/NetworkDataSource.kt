package com.hmju.memo.repository.network

import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.paging.PagingModel
import io.reactivex.Single

/**
 * Description : Network 통신후 데이터 처리 관련 클래스
 *
 * Created by hmju on 2020-09-16
 */
interface NetworkDataSource {

    fun fetchMemoList(params: MemoListParam): PagingModel<MemoItem>
}
