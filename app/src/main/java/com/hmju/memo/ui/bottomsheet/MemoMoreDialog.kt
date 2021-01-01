package com.hmju.memo.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.extension.ListMutableLiveData
import com.hmju.memo.databinding.DialogMemoDetailMoreBinding
import com.hmju.memo.utils.ResourceProvider
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

/**
 * Description : 메모 상세 더보기 Dialog Class
 * Created by juhongmin on 2020/10/03
 */
class MemoMoreDialog : RoundedBottomSheet(), KoinComponent {

    interface Listener {
        fun onItemSelected(pos: Int)
    }

    enum class Type(val type: Int) {
        NORMAL(0), TAG(1)
    }

    data class MemoDetailMoreDialogItem(
        val id: String? = null,
        @DrawableRes val iconId: Int,
        val title: String
    )

    private val provider: ResourceProvider by inject()

    // 사진 추가, 메모 삭제 데이터 추가.
    private val dataList = ListMutableLiveData<MemoDetailMoreDialogItem>().apply {
        add(
            MemoDetailMoreDialogItem(
                iconId = R.drawable.ic_add_photo,
                title = provider.getString(R.string.memo_detail_more_add_photo)
            )
        )
        add(
            MemoDetailMoreDialogItem(
                iconId = R.drawable.ic_delete,
                title = provider.getString(R.string.memo_detail_more_delete)
            )
        )
    }

    private lateinit var callback: (Int, Int) -> Unit
    lateinit var viewModel: BaseViewModel
    lateinit var binding: DialogMemoDetailMoreBinding

    private val listener: Listener = object : Listener {
        override fun onItemSelected(pos: Int) {
            callback.invoke(Type.NORMAL.type, pos)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<DialogMemoDetailMoreBinding>(
            inflater,
            R.layout.dialog_memo_detail_more,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.listener = listener
        binding.dataList = dataList.value
    }

    companion object {
        fun newInstance(
            tmpViewModel: BaseViewModel,
            tmpCallback: (Int, Int) -> Unit
        ): MemoMoreDialog =
            MemoMoreDialog().apply {
                callback = tmpCallback
                viewModel = tmpViewModel
            }
    }
}