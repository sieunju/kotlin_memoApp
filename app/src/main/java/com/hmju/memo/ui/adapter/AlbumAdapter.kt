package com.hmju.memo.ui.adapter

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.databinding.ItemAlbumBinding
import com.hmju.memo.databinding.ItemAlbumCameraBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.AlbumViewModel

/**
 * Description : 앨범 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class AlbumAdapter(
    private val viewModel: AlbumViewModel
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val photoList = arrayListOf<String>()
    private var lastPos = -1
    private var size = 0
    private var photoCursor: Cursor? = null

    fun setCursor(cursor: Cursor) {
        photoList.clear()
        photoCursor = cursor
        size = cursor.count
        notifyDataSetChanged()
        JLogger.d("Cursor Size\t$size")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, pos: Int) {
        // 카메라 캡처 ViewHolder
        if (pos == 0) {

        } else {
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

            if (holder is AlbumViewHolder) {
                holder.binding.pos = dataPos
                holder.binding.isSelected = viewModel.isSelected(dataPos)
                holder.binding.imgUrl = photoList[dataPos]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == R.layout.item_album_camera) {
            CameraCaptureViewHolder(
                parent = parent,
                layoutId = R.layout.item_album_camera,
                viewModel = viewModel
            )
        } else {
            AlbumViewHolder(
                parent = parent,
                layoutId = R.layout.item_album,
                viewModel = viewModel
            )
        }
    }

    override fun getItemViewType(pos: Int): Int {
        // 첫번째는 카메라 캡처 ViewHolder, 나머지는 사진
        return if (pos == 0) {
            R.layout.item_album_camera
        } else {
            R.layout.item_album
        }
    }

    override fun getItemCount(): Int {
        return size + 1
    }

    private val albumListener = object : Listener {
        override fun onSelected(pos: Int, view: View) {

        }
    }

    interface Listener {
        fun onSelected(pos: Int, view: View)
    }

    class CameraCaptureViewHolder(parent: ViewGroup, layoutId: Int, viewModel: AlbumViewModel) :
        BaseViewHolder<ItemAlbumCameraBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }

    class AlbumViewHolder(
        parent: ViewGroup,
        layoutId: Int,
        viewModel: AlbumViewModel
    ) :
        BaseViewHolder<ItemAlbumBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
            binding.isSelected = false
        }
    }
}