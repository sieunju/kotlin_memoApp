package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemTestHeaderBinding
import com.hmju.memo.databinding.ItemTestNormalBannerBinding
import com.hmju.memo.databinding.ItemTestNormalEventBinding
import com.hmju.memo.databinding.ItemTestUrlBannerBinding
import com.hmju.memo.model.test.*

/**
 * Description : Sealed Test Item Adapter Class
 *
 * Created by juhongmin on 2020/12/06
 */
class TestItemAdapter :
    BaseAdapter<TestBaseImgModel>() {

    override fun onCreateView(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            R.layout.item_test_header -> {
                return TestHeaderImgViewHolder(parent, viewType)
            }
            R.layout.item_test_normal_banner -> {
                return TestNormalBannerImgViewHolder(parent, viewType)
            }
            R.layout.item_test_normal_event -> {
                return TestNormalEventImgViewHolder(parent, viewType)
            }
            R.layout.item_test_url_banner -> {
                return TestNormalUrlBannerViewHolder(parent, viewType)
            }
            else -> {
                throw IllegalArgumentException("onCreateView Invalid Argument..")
            }
        }
    }

    override fun onBindView(pos: Int, holder: BaseViewHolder<*>) {
        when (holder) {
            is TestHeaderImgViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is TestNormalBannerImgViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is TestNormalEventImgViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is TestNormalUrlBannerViewHolder -> {
                holder.binding.item = getData(pos)
            }
        }
    }

    override fun getItemViewType(pos: Int): Int {
        return when (dataList[pos]) {
            is TestHeaderImg -> {
                return R.layout.item_test_header
            }
            is TestNormalBannerImg -> {
                return R.layout.item_test_normal_banner
            }
            is TestUrlBannerImg -> {
                return R.layout.item_test_normal_event
            }
            is TestNormalEventImg -> {
                return R.layout.item_test_url_banner
            }
            else -> {
                throw IllegalArgumentException("getItemViewType Invalid Argument..")
            }
        }
    }

    inner class TestHeaderImgViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemTestHeaderBinding>(parent, layoutId)

    inner class TestNormalBannerImgViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemTestNormalBannerBinding>(parent, layoutId)

    inner class TestNormalEventImgViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemTestNormalEventBinding>(parent, layoutId)

    inner class TestNormalUrlBannerViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<ItemTestUrlBannerBinding>(parent, layoutId)
}