package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.define.ListItemType

/**
 * Description : Same Type List Adapter Class..
 *
 * Created by hmju on 2020-12-28
 */
class ItemListAdapter<Type : Any> : BaseAdapter<Type>() {

    var type: ListItemType = ListItemType.A

    override fun onCreateView(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        TODO("Not yet implemented")
    }

    override fun onBindView(pos: Int, holder: BaseViewHolder<*>) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(pos: Int) = type.layoutId
}