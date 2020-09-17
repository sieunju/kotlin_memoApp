package com.hmju.memo.ui.adapter

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseAdapter
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.databinding.ItemAlbumBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.AlbumViewModel

/**
 * Description : 앨범 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class AlbumAdapter(
    private val viewModel: AlbumViewModel
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private val photoList = arrayListOf<String>()
    private var lastPos = -1
    private var size = 0
    private var photoCursor: Cursor? = null

    fun setCursor(cursor: Cursor) {
        JLogger.d("setCursor!!!!!@!@")
        photoList.clear()
        photoCursor = cursor
        size = cursor.count
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, pos: Int) {
        if (lastPos < pos) {
            lastPos = pos
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
                    holder.binding.imgUrl = photoList[pos]
                }
            }
        } else {
            holder.binding.imgUrl = photoList[pos]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            parent = parent,
            layoutId = R.layout.item_album,
            viewModel = viewModel
        )
    }

    override fun getItemCount(): Int {
        return size
    }

    class AlbumViewHolder(parent: ViewGroup, layoutId: Int, viewModel: AlbumViewModel) :
        BaseViewHolder<ItemAlbumBinding>(parent, layoutId) {
        init {
            binding.viewModel = viewModel
        }
    }
}