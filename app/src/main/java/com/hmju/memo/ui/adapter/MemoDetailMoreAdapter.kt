package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemMemoDetailMoreBinding
import com.hmju.memo.ui.bottomsheet.MemoMoreDialog

/**
 * Description : 메모 상세 더보기 Adapter Class
 *
 * Created by juhongmin on 2020/10/03
 */
class MemoDetailMoreAdapter(
    private val dataList: ArrayList<MemoMoreDialog.MemoDetailMoreDialogItem>,
    val listener: MemoMoreDialog.Listener
) : RecyclerView.Adapter<MemoDetailMoreAdapter.MemoDetailMoreItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoDetailMoreItemViewHolder {
        return MemoDetailMoreItemViewHolder(
            parent = parent,
            layoutId = R.layout.item_memo_detail_more,
            listener = listener
        )
    }

    override fun onBindViewHolder(holder: MemoDetailMoreItemViewHolder, pos: Int) {
        holder.binding.pos = pos
        holder.binding.item = dataList[pos]
    }

    override fun getItemCount() = dataList.size

    class MemoDetailMoreItemViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        listener: MemoMoreDialog.Listener
    ) : BaseViewHolder<ItemMemoDetailMoreBinding>(parent, layoutId) {
        init {
            binding.listener = listener
        }
    }
}