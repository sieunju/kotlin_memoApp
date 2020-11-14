package com.hmju.memo.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hmju.memo.R
import com.hmju.memo.databinding.DialogSelectedFilterBottomSheetBinding
import com.hmju.memo.model.gallery.GalleryFilterItem
import com.hmju.memo.ui.bottomsheet.RoundedBottomSheet
import com.hmju.memo.viewmodels.GalleryViewModel

/**
 * Description : 갤러리 필터 선택 BottomSheet Class
 *
 * Created by juhongmin on 2020/11/12
 */
class SelectedFilterBottomSheet(
    private val dataList: ArrayList<GalleryFilterItem>
) : RoundedBottomSheet() {

    interface Listener {
        fun onItemSelected(item : GalleryFilterItem)
    }

    private val listener : Listener = object : Listener {
        override fun onItemSelected(item: GalleryFilterItem) {
            callback.invoke(item)
        }
    }

    private lateinit var binding: DialogSelectedFilterBottomSheetBinding
    private lateinit var callback : (GalleryFilterItem) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<DialogSelectedFilterBottomSheetBinding>(
            inflater,
            R.layout.dialog_selected_filter_bottom_sheet,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.apply {
            BottomSheetBehavior
                .from(findViewById<FrameLayout>(R.id.design_bottom_sheet))
                .peekHeight = context.resources.getDimensionPixelOffset(R.dimen.size_200)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = listener
        binding.dataList = dataList
    }

    companion object {
        fun newInstance(
            dataList: ArrayList<GalleryFilterItem>,
            callback : (GalleryFilterItem) -> Unit
        ): SelectedFilterBottomSheet = SelectedFilterBottomSheet(dataList).apply {
            this.callback = callback
        }
    }
}