package com.hmju.memo.ui.adapter

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGalleryCameraBinding
import com.hmju.memo.databinding.ItemGalleryPhotoBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewmodels.GalleryViewModel

/**
 * Description : 갤러리 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class GalleryAdapter(
    private val viewModel: GalleryViewModel
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val photoList = arrayListOf<String>()
    private var lastPos = -1
    private var size = 0
    private var photoCursor: Cursor? = null

    fun setCursor(cursor: Cursor) {
        // init Variable
        photoList.clear()
        lastPos = -1
        size = cursor.count

        JLogger.d("Cursor Size\t$size")

        // set Cursor
        if(photoCursor != null && !photoCursor!!.isClosed) {
            photoCursor!!.close()
        }
        photoCursor = cursor
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        // 사진 ViewHolder
        if(pos != 0) {
            val dataPos = pos - 1
            if (lastPos < dataPos) {
                lastPos = dataPos
                photoCursor?.let {
                    if (it.moveToNext()) {
                        val contentId = it.getLong(
                            it.getColumnIndex(MediaStore.Images.Media._ID)
                        )

                        val uri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentId.toString()
                        )

                        photoList.add(uri.toString())
                    }
                }
            }

            if (holder is PhotoViewHolder) {
                holder.binding.imgUrl = photoList[dataPos]
            }

            if(lastPos == size + 1) {
                JLogger.d("맨 마지막입니다.")
                photoCursor?.close()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == R.layout.item_gallery_camera) {
            CameraCaptureViewHolder(
                parent = parent,
                layoutId = R.layout.item_gallery_camera,
                viewModel = viewModel
            )
        } else {
            PhotoViewHolder(
                parent = parent,
                layoutId = R.layout.item_gallery_photo,
                viewModel = viewModel
            )
        }
    }

    override fun getItemViewType(pos: Int): Int {
        // 첫번째는 카메라 캡처 ViewHolder, 나머지는 사진
        return if (pos == 0) {
            R.layout.item_gallery_camera
        } else {
            R.layout.item_gallery_photo
        }
    }

    override fun getItemCount(): Int {
        return if(size == 0) {
            0
        } else {
            size + 1
        }
    }

    class CameraCaptureViewHolder(parent: ViewGroup, layoutId: Int, viewModel: GalleryViewModel) :
        BaseViewHolder<ItemGalleryCameraBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }

    class PhotoViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        viewModel: GalleryViewModel
    ) :
        BaseViewHolder<ItemGalleryPhotoBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }
}