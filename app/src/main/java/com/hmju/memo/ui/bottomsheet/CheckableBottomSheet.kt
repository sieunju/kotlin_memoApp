package com.hmju.memo.ui.bottomsheet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hmju.memo.R
import com.hmju.memo.databinding.DialogCheckableBottomSheetBinding

/**
 * Description : 타이틀 및 체크 기능 UI 를 가진 BottomSheet Dialog Class
 *
 * Created by juhongmin on 2020/09/21
 */
class CheckableBottomSheet : RoundedBottomSheet() {

    interface Listener {
        fun onItemSelected(pos: Int, name: String)
    }

    data class CheckableBottomSheetItem(
        val id: String? = null,
        val name: String,
        val isSelected: Boolean
    )

    private lateinit var dataList: List<CheckableBottomSheetItem>
    private lateinit var callback: (Int, String) -> Unit
    lateinit var binding: DialogCheckableBottomSheetBinding
    private var peekHeight = -1

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
        return DataBindingUtil.inflate<DialogCheckableBottomSheetBinding>(
            inflater,
            R.layout.dialog_checkable_bottom_sheet,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 고정된 높이값이 있는 경우 세팅.
        if (peekHeight != -1) {
            dialog?.apply {
                BottomSheetBehavior
                    .from(findViewById<FrameLayout>(R.id.design_bottom_sheet))
                    .peekHeight = peekHeight
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = listener
        binding.dataList = dataList
    }

    companion object {
        fun newInstance(
            tmpDataList: List<CheckableBottomSheetItem>,
            tmpCallback: (Int, String) -> Unit
        ): CheckableBottomSheet =
            CheckableBottomSheet().apply {
                dataList = tmpDataList
                callback = tmpCallback
            }

        fun newInstance(
            tmpPeekHeight: Int,
            tmpDataList: List<CheckableBottomSheetItem>,
            tmpCallback: (Int, String) -> Unit
        ): CheckableBottomSheet =
            CheckableBottomSheet().apply {
                dataList = tmpDataList
                callback = tmpCallback
                peekHeight = tmpPeekHeight
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setWhiteNavigationBar()
                }
            }
    }
}