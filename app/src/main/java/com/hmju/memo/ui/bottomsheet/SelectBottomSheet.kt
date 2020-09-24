package com.hmju.memo.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hmju.memo.R
import com.hmju.memo.databinding.DialogSelectBottomSheetBinding

/**
 * Description :
 *
 * Created by juhongmin on 2020/09/21
 */
class SelectBottomSheet : RoundedBottomSheet() {

    interface Listener {
        fun onItemSelected(pos: Int, name: String)
    }

    data class BottomSheetSelect(
        val id: String? = null,
        val name: String,
        val isSelected: Boolean,
        val data: Any? = null
    )

    private lateinit var dataList: List<BottomSheetSelect>
    private lateinit var callback: (Int, String) -> Unit
    lateinit var binding: DialogSelectBottomSheetBinding

    private val listener: Listener = object : Listener {
        override fun onItemSelected(pos: Int, name: String) {
            callback.invoke(pos, name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<DialogSelectBottomSheetBinding>(
            inflater,
            R.layout.dialog_select_bottom_sheet,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = listener
        binding.dataList = dataList
    }

    companion object {
        fun newInstance(
            tmpDataList: List<BottomSheetSelect>,
            tmpCallback: (Int,String) -> Unit
        ): SelectBottomSheet =
            SelectBottomSheet().apply {
                dataList = tmpDataList
                callback = tmpCallback
            }
    }
}