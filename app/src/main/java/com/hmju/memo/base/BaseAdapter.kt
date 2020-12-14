package com.hmju.memo.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.convenience.ListMutableLiveData

/**
 * Description : BaseAdapter Class
 *
 * Created by hmju on 2020-06-12
 */
abstract class BaseAdapter<BaseModel : Any> : RecyclerView.Adapter<BaseViewHolder<*>>() {

    abstract fun onCreateView(parent: ViewGroup, viewType: Int): BaseViewHolder<*>
    abstract fun onBindView(pos: Int, holder: BaseViewHolder<*>)

    var dataList = arrayListOf<BaseModel>()
        set(value) {
            if (value.size > 0) {
                field.clear()
                field.addAll(value)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateView(parent, viewType)

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        onBindView(pos, holder)
    }

    override fun getItemCount() = dataList.size

    protected inline fun <reified DataType> getData(
        pos: Int
    ): DataType? {
        return if (dataList.size > pos && dataList[pos] is DataType) {
            dataList[pos] as DataType
        } else {
            null
        }
    }
}