package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemBottomSheetSelectBinding
import com.hmju.memo.ui.bottomsheet.SelectBottomSheet

/**
 * Description : BottomSheetDialog 기반 Select Adapter  Class
 *
 * Created by juhongmin on 2020/09/21
 */
class BottomSheetSelectAdapter(
    private val dataList: List<SelectBottomSheet.BottomSheetSelect>,
    val listener: SelectBottomSheet.Listener
) :
    RecyclerView.Adapter<BottomSheetSelectAdapter.SelectItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectItemViewHolder {
        return SelectItemViewHolder(
            parent = parent,
            layoutId = R.layout.item_bottom_sheet_select,
            listener = listener
        )
    }

    override fun onBindViewHolder(holder: SelectItemViewHolder, pos: Int) {
        val item = dataList[pos]
        holder.binding.pos = pos
        holder.binding.item = item
    }

    override fun getItemCount() = dataList.size

    class SelectItemViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        listener: SelectBottomSheet.Listener
    ) :
        BaseViewHolder<ItemBottomSheetSelectBinding>(parent, layoutId) {
        init {
            binding.listener = listener
        }
    }
}
