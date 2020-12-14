package com.hmju.memo.ui.adapter

import android.view.ViewGroup
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.*
import com.hmju.memo.model.test.TestUiModel

/**
 * Description : Sealed Test Adapter Class
 *
 * Created by juhongmin on 2020/12/06
 */
class TestListAdapter : BaseAdapter<TestUiModel>() {

    override fun onCreateView(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            R.layout.layout_test_header -> {
                return TestHeaderViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0001 -> {
                return A0001ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0002 -> {
                return A0002ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0002_b0001 -> {
                return A0002AndB0001ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0003_b0001 -> {
                return A0003AndB0001ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0004_b0002 -> {
                return A0004AndB0002ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            R.layout.layout_test_a0005_b0003 -> {
                return A0005AndB0003ViewHolder(
                    parent = parent,
                    layoutId = viewType
                )
            }
            else -> {
                throw IllegalArgumentException("onCreateView Invalid Type..")
            }
        }
    }

    override fun onBindView(pos: Int, holder: BaseViewHolder<*>) {
        when (holder) {
            is TestHeaderViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is A0001ViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is A0002ViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is A0002AndB0001ViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is A0003AndB0001ViewHolder -> {
                holder.binding.item = getData(pos)
            }
            is A0004AndB0002ViewHolder -> {
                holder.binding.item = getData(pos)
            }
        }
    }

    override fun getItemViewType(pos: Int): Int {
        return when (dataList[pos]) {
            is TestUiModel.TestHeaderModel -> {
                R.layout.layout_test_header
            }
            is TestUiModel.A0001Model -> {
                R.layout.layout_test_a0001
            }
            is TestUiModel.A0002Model -> {
                R.layout.layout_test_a0002
            }
            is TestUiModel.A0002AndB0001Model -> {
                R.layout.layout_test_a0002_b0001
            }
            is TestUiModel.A0003AndB0001Model -> {
                R.layout.layout_test_a0003_b0001
            }
            is TestUiModel.A0004AndB0002Model -> {
                R.layout.layout_test_a0004_b0002
            }
            is TestUiModel.A0005AndB0003Model -> {
                R.layout.layout_test_a0005_b0003
            }
        }
    }

    inner class TestHeaderViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestHeaderBinding>(parent, layoutId)

    inner class A0001ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0001Binding>(parent, layoutId)

    inner class A0002ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0002Binding>(parent, layoutId)

    inner class A0002AndB0001ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0002B0001Binding>(parent, layoutId)

    inner class A0003AndB0001ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0003B0001Binding>(parent, layoutId)

    inner class A0004AndB0002ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0004B0002Binding>(parent, layoutId)

    inner class A0005AndB0003ViewHolder(parent: ViewGroup, layoutId: Int) :
        BaseViewHolder<LayoutTestA0005B0003Binding>(parent, layoutId)
}