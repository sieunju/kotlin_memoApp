package com.hmju.memo.repository.network

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.bumptech.glide.load.resource.file.FileResource
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.network.paging.MemoListDataSourceFactory
import com.hmju.memo.repository.network.paging.PagingModel
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Maybe
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Description : Network 통신후 데이터 처리를 위한 클래스,
 * 파라미터는 추후 추가될수도 있음.
 *
 * Created by hmju on 2020-09-16
 */
class NetworkDataSourceImpl(
    private val apiService: ApiService,
    private val actPref: AccountPref,
    private val provider: ResourceProvider
) : NetworkDataSource {

    override fun fetchMemoList(params: MemoListParam): PagingModel<MemoItem> {
        val factory = MemoListDataSourceFactory(
            apiService = apiService,
            actPref = actPref,
            memoParams = params
        )
        val pagedList = factory.toLiveData(
            Config(
                pageSize = 20,
                initialLoadSizeHint = 20,
                enablePlaceholders = true
            )
        )

        return PagingModel(
            pagedList = pagedList,
            networkState = Transformations.switchMap(factory.sourceLiveData) { it.networkState }
        )
    }
}