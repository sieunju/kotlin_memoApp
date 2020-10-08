package com.hmju.memo.ui.bindingadapter

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.view.DragEvent
import android.view.View
import androidx.databinding.BindingAdapter
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.viewModels.ImageEditViewModel
import com.hmju.memo.widget.flexibleImageView.FlexibleImageView

/**
 * Description : 이미지 편집 페이지 관련 BindingAdapter Class
 *
 * Created by juhongmin on 2020/10/06
 */

@Suppress("DEPRECATION")
@BindingAdapter(value = ["viewModel", "rootView"], requireAll = false)
fun setFlexibleListener(
    imageView: FlexibleImageView,
    viewModel: BaseViewModel,
    rootView: View
) {

    imageView.setOnLongClickListener { view ->
        // 태그 생성
        val item = ClipData.Item(rootView.tag.toString())
        val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)

        val data = ClipData(rootView.tag.toString(), mimeType, item)
        val shadow = View.DragShadowBuilder(rootView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rootView.startDragAndDrop(data, shadow, rootView, 0)
        } else {
            rootView.startDrag(data, shadow, rootView, 0)
        }

        if (viewModel is ImageEditViewModel) {
            viewModel.startDragAndDrop(view)
        }

        return@setOnLongClickListener true
    }

    rootView.setOnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                // Drop 한경우.
                if (viewModel is ImageEditViewModel) {
                    viewModel.onDragAndDrop(view = v)
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                // 드래그 영역이 부모 View 밖으로 빠져 나가는 경우
                if (viewModel is ImageEditViewModel) {
                    viewModel.onDragNothing()
                }
            }
        }

        return@setOnDragListener true
    }
}