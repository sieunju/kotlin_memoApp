package com.hmju.memo.base

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * Description : AAC Paging Adapter class
 *
 * Created by juhongmin on 2020/06/21
 */
abstract class BasePagedAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, VH>(diffCallback) {

}