package com.hmju.memo.ui.adapter

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hmju.memo.R
import com.hmju.memo.base.BaseViewHolder
import com.hmju.memo.databinding.ItemGalleryCameraBinding
import com.hmju.memo.databinding.ItemGalleryPhotoBinding
import com.hmju.memo.utils.JLogger
import com.hmju.memo.viewModels.GalleryViewModel

/**
 * Description : 갤러 Adapter Class
 *
 * Created by juhongmin on 2020/06/21
 */
class GalleryAdapter(
    private val viewModel: GalleryViewModel
) : RecyclerView.Adapter<BaseViewHolder<*>>(), Filterable {

    data class Item(
        val id: String,
        val category: String
    )

    //    private val photoList = arrayListOf<String>()
    private val photoList = arrayListOf<Item>()
    private var filteredList = arrayListOf<Item>()
    private var unFilteredList = arrayListOf<Item>()
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

                        val filterName =
                            it.getString(it.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                        viewModel.filterGallery.value.add(filterName)

                        photoList.add(
                            Item(
                                id = uri.toString(),
                                category = filterName
                            )
                        )
//                        photoList.add(uri.toString())
                    }
                }
            }

            if (holder is PhotoViewHolder) {
                holder.binding.pos = dataPos
                holder.binding.isSelected = viewModel.isSelected(dataPos)
                holder.binding.imgUrl = photoList[dataPos].id

                JLogger.d("Album Path\t${photoList[dataPos].category}")
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
        return size + 1
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                constraint?.let{
                    JLogger.d("Filter\t$it")

                    TODO("Not yet implemented")

                } ?: run {
                    return null
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                TODO("Not yet implemented")
            }
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
            binding.isSelected = false
        }
    }
}