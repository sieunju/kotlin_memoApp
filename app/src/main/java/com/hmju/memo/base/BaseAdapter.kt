package com.hmju.memo.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.convenience.ListMutableLiveData

/**
 * Description : BaseAdapter Class
 *
 * Created by hmju on 2020-06-12
 */
abstract class BaseAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {

    protected val mItems = ListMutableLiveData<ItemStruct<*>>()

    // [s] View Type Define
    protected val TYPE_MEMO_NORMAL = 1
    protected val TYPE_MEMO_IMG = 2
    // [e] View Type Define

    data class ItemStruct<ITEM>(val data: ITEM,val type: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return mItems.size()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        TODO("Not yet implemented")
    }
}