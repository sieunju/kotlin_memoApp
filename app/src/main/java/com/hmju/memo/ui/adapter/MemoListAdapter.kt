package com.hmju.memo.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import com.hmju.memo.R
import com.hmju.memo.base.BasePagedAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemHorizontalLoadingBinding
import com.hmju.memo.databinding.ItemMemoImgBinding
import com.hmju.memo.databinding.ItemMemoNormalBinding
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.MainViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 * Description : 메모 리스트 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class MemoListAdapter(
    val viewModel: MainViewModel
) : BasePagedAdapter<MemoItem, BaseViewHolder<*>>(DIFF_CALLBACK), KoinComponent {

    private val context: Context by inject()
    private var lastPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            R.layout.item_memo_normal -> {
                return MemoNormalViewHolder(
                    parent = parent,
                    layoutId = viewType,
                    viewModel = viewModel
                )
            }
            R.layout.item_memo_img -> {
                return MemoImgViewHolder(
                    parent = parent,
                    layoutId = viewType,
                    viewModel = viewModel
                )
            }
            else -> {
                return LoadingViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        when (holder) {
            is MemoNormalViewHolder -> {
                holder.binding.pos = pos
                holder.binding.item = getItem(pos)
            }
            is MemoImgViewHolder -> {
                holder.binding.pos = pos
                holder.binding.item = getItem(pos)
            }
        }

        setAnimation(holder.itemView, pos)
    }

    /**
     * Index 갱신 처리.
     * @param pos 변경하고 싶은 위치값
     * @param tmpItem 변경하고 싶은 데이터
     */
    fun setChangedData(pos: Int, tmpItem: MemoItem) {
        val item = getItem(pos)
        item?.let {
            // 우선순위 같은 경우에만 갱신 처리
            if (it.tag == tmpItem.tag) {
                it.title = tmpItem.title
                it.contents = tmpItem.contents
                notifyItemChanged(pos)
            }
        }
    }

    /**
     * 좌 -> 우 및  Fade 효과도 있는 애니메이션
     */
    private fun setAnimation(view: View, pos: Int) {
        if (pos > lastPos) {
            view.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left))
            lastPos = pos
        }
    }

    override fun getItemViewType(pos: Int): Int {
        getItem(pos)?.let { item ->
            return if (item.isNormal) {
                R.layout.item_memo_normal
            } else {
                R.layout.item_memo_img
            }
        } ?: return R.layout.item_horizontal_loading
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemoItem>() {
            override fun areItemsTheSame(oldItem: MemoItem, newItem: MemoItem) =
                oldItem.manageNo == newItem.manageNo

            override fun areContentsTheSame(oldItem: MemoItem, newItem: MemoItem) =
                oldItem == newItem
        }
    }

    class MemoNormalViewHolder(parent: ViewGroup, layoutId: Int, viewModel: MainViewModel) :
        BaseViewHolder<ItemMemoNormalBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }

    class MemoImgViewHolder(parent: ViewGroup, layoutId: Int, viewModel: MainViewModel) :
        BaseViewHolder<ItemMemoImgBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }

    class LoadingViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemHorizontalLoadingBinding>(parent, layoutId)
}