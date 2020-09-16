package com.hmju.memo.repository.network.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.hmju.memo.define.NetworkState

/**
 * Description : 페이징 관련 데이터 모델.
 *
 * Created by hmju on 2020-09-16
 */
data class PagingModel<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>
)