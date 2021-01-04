package com.hmju.memo.widget.glide

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hmju.memo.R
import com.hmju.memo.widget.glide.transformation.BaseTransformation
import com.hmju.memo.widget.glide.transformation.BorderTransformation
import com.hmju.memo.widget.glide.transformation.CornerTransformation
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Description :
 *
 * Created by hmju on 2020-12-22
 */
class GlideUtil {
    interface Listener {
        fun onFailed(msg: String?)

        /**
         * Gif Resource Convert Example
         * if(drawable instanceof GifDrawable) {
         * GifDrawable gif = (GifDrawable) drawable;
         * }
         *
         *
         * Bitmap Resource Convert Example
         * if(drawable instanceof BitmapDrawable) {
         * Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
         * }
         *
         * @param drawable Current Resource
         */
        fun onResource(drawable: Drawable?)
    }

    private val gPlaceHolder = intArrayOf(
        R.color.colorPlaceHolder_1,
        R.color.colorPlaceHolder_2,
        R.color.colorPlaceHolder_3,
        R.color.colorPlaceHolder_4
    )
    private val placeHolderSize = gPlaceHolder.size

    enum class ScaleType {
        CENTER_CROP, FIT_CENTER, CENTER_INSIDE, FIT_XY
    }

    private var mUri: String? = null
    private var mResId = -1
    private var mScaleType: ScaleType? = null
    private var mOptions: RequestOptions? = null
//    @FloatRange(from = 0.0F, to = 1.0F)
//    private float mThumbNail = 0F; 썸네일은 적용 X 필요 없을듯

    //    @FloatRange(from = 0.0F, to = 1.0F)
    //    private float mThumbNail = 0F; 썸네일은 적용 X 필요 없을듯

    companion object {

        @Volatile
        private var instance: GlideUtil? = null

        fun with(): GlideUtil {
            instance?.let {
                it.reset()
                return it
            } ?: run {
                synchronized(this) {
                    return instance ?: GlideUtil().also {
                        it.reset()
                        instance = it
                    }
                }
            }
        }
    }

    /**
     * Reset Options Func
     */
    private fun reset() {
        mUri = null
        mResId = -1
        if (mOptions != null) {
            mOptions = null
        }
        if (mScaleType != null) {
            mScaleType = null
        }

        // Default Options.
        // No Uri or No Image 인경우 보이는 화면 -> fallback
        // 이미지 로딩시 보이는 화면 -> PlaceHolder
        // 이미지 로딩 실패시 -> error

        mOptions = RequestOptions()
            .fallback(gPlaceHolder[Random().nextInt(placeHolderSize)])
            .placeholder(gPlaceHolder[Random().nextInt(placeHolderSize)])
            .error(R.drawable.ic_error)
    }

    /**
     * Image Load FUnc
     *
     * @param uri Image Load Uri
     * @return GlideUtil
     */
    fun load(uri: Uri): GlideUtil {
        return load(uri.toString())
    }

    /**
     * Image Load Func
     *
     * @param path Image Path
     * @return GlideUtil
     */
    fun load(path: String): GlideUtil {
        mUri = path
        return this
    }

    /**
     * Image Load Func
     *
     * @param id Local Resource Id
     * @return GlideUtil
     */
    fun load(@RawRes @DrawableRes id: Int): GlideUtil {
        mResId = id
        return this
    }

    /**
     * set Scale Func.
     *
     * @param type ScaleType
     * @return GlideUtil
     */
    fun scaleType(type: ScaleType?): GlideUtil {
        mScaleType = type
        when (type) {
            ScaleType.CENTER_CROP -> mOptions!!.centerCrop()
            ScaleType.FIT_CENTER -> mOptions!!.fitCenter()
            ScaleType.CENTER_INSIDE -> mOptions!!.centerInside()
        }
        return this
    }

    /**
     * Image Resize Func.
     *
     * @param size Width == Height
     * @return GlideUtil
     */
    fun resize(size: Int): GlideUtil {
        return resize(size, size)
    }

    /**
     * Image Resize Func.
     *
     * @param width  Resize Width
     * @param height Resize Height
     * @return GlideUtil
     */
    fun resize(width: Int, height: Int): GlideUtil {
        mOptions = mOptions!!.override(width, height)
        return this
    }

    /**
     * Set No Image FallBack
     *
     * @param resourceId Resource Id
     * @return GlideUtil
     */
    fun fallBack(@DrawableRes resourceId: Int): GlideUtil {
        mOptions = mOptions!!.fallback(resourceId)
        return this
    }

    /**
     * Set Image Loading PlaceHolder
     *
     * @param resourceId Resource Id
     * @return GlideUtil
     */
    fun placeHolder(@DrawableRes resourceId: Int): GlideUtil {
        mOptions = mOptions!!.placeholder(resourceId)
        return this
    }

    /**
     * Image Fail Func.
     *
     * @param id Drawable Id
     * @return GlideUtil
     */
    fun failDrawable(@DrawableRes id: Int): GlideUtil {
        mOptions = mOptions!!.error(id)
        return this
    }

    /**
     * Image Fail Color
     *
     * @param id Color Id
     * @return GlideUtil
     */
    fun failColor(@ColorRes id: Int): GlideUtil {
        mOptions = mOptions!!.error(id)
        return this
    }

//    /**
//     * Image Thumb Setting
//     *
//     * @param offset Thumb Offset
//     * @return GlideUtils
//     */
//    public GlideUtil thumbNail(@FloatRange(from = 0.0F, to = 1.0F) float offset) {
//        mThumbNail = offset;
//        return this;
//    }

    //    /**
    //     * Image Thumb Setting
    //     *
    //     * @param offset Thumb Offset
    //     * @return GlideUtils
    //     */
    //    public GlideUtil thumbNail(@FloatRange(from = 0.0F, to = 1.0F) float offset) {
    //        mThumbNail = offset;
    //        return this;
    //    }
    /**
     * Image Transforms Func.
     * 로딩한 이미지를 편집할수 있는 함수.
     *
     * @param transformations Round, Corner, Scale...
     * @return GlideUtil
     */
    fun transforms(transformations: Transformation<Bitmap?>): GlideUtil {
        mOptions = mOptions!!.transform(transformations)
        return this
    }

    /**
     * Image Corner Func.
     *
     * @param ctx            Context
     * @param cornerRadiusId Corner Size
     * @return GlideUtil
     */
    fun corner(
        ctx: Context,
        @DimenRes cornerRadiusId: Int
    ): GlideUtil {
        return corner(ctx, BaseTransformation.ALL, cornerRadiusId)
    }

    /**
     * Image Corner Func
     * Corner Radius 부분적으로 설정할수 있는 함수.
     *
     * @param ctx            Context
     * @param type           TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
     * @param cornerRadiusId Corner Radius Id
     * @return GlideUtil
     */
    fun corner(
        ctx: Context,
        @BaseTransformation.CornerType type: Int,
        @DimenRes cornerRadiusId: Int
    ): GlideUtil {
        return corner(type, ctx.resources.getDimensionPixelOffset(cornerRadiusId))
    }

    /**
     * Image Corner Func.
     *
     * @param type         TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
     * @param cornerRadius Corner Radius
     * @return GlideUtil
     */
    fun corner(
        @BaseTransformation.CornerType type: Int,
        cornerRadius: Int
    ): GlideUtil {
        check(mScaleType == null) { "transform 사용시 ScaleType 을 설정하면 안됩니다." }
        mOptions = mOptions!!.transform(
            MultiTransformation(
                CenterCrop(),
                CornerTransformation(type).corner(cornerRadius)
            )
        )
        return this
    }

    /**
     * Image Border Func.
     *
     * @param ctx           Context
     * @param borderWidthId Border Width Id
     * @param borderColorId Border Color Id
     * @return GlideUtil
     */
    fun border(
        ctx: Context,
        @DimenRes borderWidthId: Int,
        @ColorRes borderColorId: Int
    ): GlideUtil {
        return border(
            ctx.resources.getDimensionPixelOffset(borderWidthId),
            ContextCompat.getColor(ctx, borderColorId)
        )
    }

    /**
     * Image Border Func
     *
     * @param borderWidth Border Width
     * @param borderColor Border Color
     * @return GlideUtil
     */
    fun border(borderWidth: Int, borderColor: Int): GlideUtil {
        check(mScaleType == null) { "transform 사용시 ScaleType 을 설정하면 안됩니다." }
        mOptions = mOptions!!.transform(
            MultiTransformation(
                CenterCrop(),
                BorderTransformation().border(borderWidth, borderColor)
            )
        )
        return this
    }

    /**
     * Image Corner Radius And Border Func.
     * Type.ALL
     *
     * @param ctx            Context
     * @param cornerRadiusId Corner Radius Id
     * @param borderWidthId  Border Width Id
     * @param borderColorId  Border Color Id
     * @return GlideUtil
     */
    fun cornerAndBorder(
        ctx: Context,
        @DimenRes cornerRadiusId: Int,
        @DimenRes borderWidthId: Int,
        @ColorRes borderColorId: Int
    ): GlideUtil {
        return cornerAndBorder(
            ctx,
            BaseTransformation.ALL,
            cornerRadiusId,
            borderWidthId,
            borderColorId
        )
    }

    /**
     * Image Corner Radius And Border Func.
     *
     * @param ctx           Context
     * @param type          CornerType TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
     * @param radiusId      Corner Radius Id
     * @param borderWidthId Border Width Id
     * @param borderColorId Border Color Id
     * @return GlideUtil
     */
    fun cornerAndBorder(
        ctx: Context,
        @BaseTransformation.CornerType type: Int,
        @DimenRes radiusId: Int,
        @DimenRes borderWidthId: Int,
        @ColorRes borderColorId: Int
    ): GlideUtil {
        return cornerAndBorder(
            type,
            ctx.resources.getDimensionPixelOffset(radiusId),
            ctx.resources.getDimensionPixelOffset(borderWidthId),
            ContextCompat.getColor(ctx, borderColorId)
        )
    }

    /**
     * Image Corner Radius And Border Func.
     *
     * @param type         CornerType TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
     * @param cornerRadius Corner Radius Size
     * @param borderWidth  Border Width
     * @param borderColor  Border Color
     * @return GlideUtil
     */
    fun cornerAndBorder(
        @BaseTransformation.CornerType type: Int,
        cornerRadius: Int,
        borderWidth: Int,
        borderColor: Int
    ): GlideUtil {
        check(mScaleType == null) { "transform 사용시 ScaleType 을 설정하면 안됩니다." }
        mOptions = mOptions!!.transform(
            MultiTransformation(
                CenterCrop(),
                CornerTransformation(type)
                    .corner(cornerRadius)
                    .border(borderWidth, borderColor)
            )
        )
        return this
    }


    /**
     * 캐시된 메모리 사용하지 않고
     * 계속해서 이미지 로드해서 사용하는 함수.
     *
     * @return GlideUtil
     */
    fun skinCached(): GlideUtil {
        mOptions?.also {
            it.diskCacheStrategy(DiskCacheStrategy.NONE)
            it.skipMemoryCache(true)
        }
        return this
    }

    fun into(imgView: AppCompatImageView) {
        into(imgView, null)
    }

    /**
     * 이미지 로딩 시작 함수.
     *
     * @param imgView Show ImageView
     */
    fun into(imgView: AppCompatImageView, listener: Listener?) {
        val act = getActivity(imgView)
        // java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
        // 방어 코드
        if (act == null || act.isFinishing) return
        // 이미지 로딩 경로 유효성 검사
        if (mUri == null && mResId == -1) return

        // 이미지 타입에 따라서 ImageView 타입 수정.
        if (mScaleType != null) {
            when (mScaleType) {
                ScaleType.CENTER_CROP -> imgView.scaleType = ImageView.ScaleType.CENTER_CROP
                ScaleType.FIT_XY -> imgView.scaleType = ImageView.ScaleType.FIT_XY
                ScaleType.CENTER_INSIDE -> imgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                else -> {}
            }
        }
        Glide.with(imgView)
            .load(if (mUri != null) mUri else mResId)
            .apply(mOptions!!)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    if (listener != null && e != null) {
                        listener.onFailed(e.message)
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (listener != null && resource != null) {
                        listener.onResource(resource)
                    }
                    return false
                }
            })
            .into(imgView)
    }

    /**
     * Cast View to Activity
     *
     * @param view View
     * @return Activity
     */
    private fun getActivity(view: View): Activity? {
        var context = view.context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    /**
     * Clear Cache And Disk Memory
     *
     * @param context Context
     */
    fun clearCache(context: Context) {
        Flowable.just(clearMemory(context))
            .subscribeOn(Schedulers.computation())
            .subscribe()
    }

    private fun clearMemory(context: Context): Boolean {
        Glide.get(context).clearDiskCache()
        Glide.get(context).clearMemory()
        return true
    }
}