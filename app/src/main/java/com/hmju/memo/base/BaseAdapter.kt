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

    abstract fun onDataChanged() // 데이터가 변경되거나 추가가 되었을 경우 호출해야 하는 함수.

    // [s] View Type Define
    protected val TYPE_MEMO_NORMAL = 1
    protected val TYPE_MEMO_IMG = 2
    // [e] View Type Define

    data class ItemStruct<ITEM>(val data: ITEM,val type: Int)

    override fun getItemCount(): Int {
        return mItems.size()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {

    }

    override fun getItemViewType(pos: Int): Int {
        return mItems.get(pos).type
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<*>) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)
    }
}