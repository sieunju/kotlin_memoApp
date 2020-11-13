package com.hmju.memo.widget.glide;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hmju.memo.R;
import com.hmju.memo.widget.glide.transformation.BaseTransformation;
import com.hmju.memo.widget.glide.transformation.BorderTransformation;
import com.hmju.memo.widget.glide.transformation.CornerTransformation;

import java.util.Random;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: Glide Util Class
 * <p>
 * Created by hmju on 2020-11-09
 */
public class GlideUtil {

    public interface Listener {
        void onFailed(String msg);

        /**
         * Gif Resource Convert Example
         * if(drawable instanceof GifDrawable) {
         * GifDrawable gif = (GifDrawable) drawable;
         * }
         * <p>
         * Bitmap Resource Convert Example
         * if(drawable instanceof BitmapDrawable) {
         * Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
         * }
         *
         * @param drawable Current Resource
         */
        void onResource(Drawable drawable);
    }

    private static int[] gPlaceHolder = new int[]{
            R.color.colorPlaceHolder_1,
            R.color.colorPlaceHolder_2,
            R.color.colorPlaceHolder_3,
            R.color.colorPlaceHolder_4
    };
    private static int placeHolderSize = gPlaceHolder.length;

    public enum ScaleType {
        CENTER_CROP, FIT_CENTER, CENTER_INSIDE, FIT_XY
    }

    private static volatile GlideUtil instance;

    protected GlideUtil() {
    }

    private String mUri = null;
    private Integer mResId = -1;
    private ScaleType mScaleType = null;
    private RequestOptions mOptions = null;
//    @FloatRange(from = 0.0F, to = 1.0F)
//    private float mThumbNail = 0F; 썸네일은 적용 X 필요 없을듯

    public static GlideUtil with() {
        if (instance == null) {
            synchronized (GlideUtil.class) {
                if (instance == null) {
                    instance = new GlideUtil();
                }
            }
        }

        instance.reset();

        return instance;
    }

    /**
     * Reset Options Func
     */
    private void reset() {
        mUri = null;
        mResId = -1;

        if (mOptions != null) {
            mOptions = null;
        }

        if (mScaleType != null) {
            mScaleType = null;
        }

        // Default Options.
        // No Uri or No Image 인경우 보이는 화면 -> fallback
        // 이미지 로딩시 보이는 화면 -> PlaceHolder
        // 이미지 로딩 실패시 -> error
        mOptions = new RequestOptions()
                .fallback(gPlaceHolder[new Random().nextInt(placeHolderSize)])
                .placeholder(gPlaceHolder[new Random().nextInt(placeHolderSize)])
                .error(R.drawable.ic_error);

//        mThumbNail = 0F;
    }

    /**
     * Image Load FUnc
     *
     * @param uri Image Load Uri
     * @return GlideUtil
     */
    public GlideUtil load(@NonNull Uri uri) {
        return load(uri.toString());
    }

    /**
     * Image Load Func
     *
     * @param path Image Path
     * @return GlideUtil
     */
    public GlideUtil load(@NonNull String path) {
        mUri = path;
        return this;
    }

    /**
     * Image Load Func
     *
     * @param id Local Resource Id
     * @return GlideUtil
     */
    public GlideUtil load(@RawRes @DrawableRes @NonNull Integer id) {
        mResId = id;
        return this;
    }

    /**
     * set Scale Func.
     *
     * @param type ScaleType
     * @return GlideUtil
     */
    public GlideUtil scaleType(ScaleType type) {
        mScaleType = type;
        switch (type) {
            case CENTER_CROP:
                mOptions.centerCrop();
                break;
            case FIT_CENTER:
                mOptions.fitCenter();
                break;
            case CENTER_INSIDE:
                mOptions.centerInside();
                break;
        }
        return this;
    }

    /**
     * Image Resize Func.
     *
     * @param size Width == Height
     * @return GlideUtil
     */
    public GlideUtil resize(int size) {
        return resize(size, size);
    }

    /**
     * Image Resize Func.
     *
     * @param width  Resize Width
     * @param height Resize Height
     * @return GlideUtil
     */
    public GlideUtil resize(int width, int height) {
        mOptions = mOptions.override(width, height);
        return this;
    }

    /**
     * Set No Image FallBack
     *
     * @param resourceId Resource Id
     * @return GlideUtil
     */
    public GlideUtil fallBack(@DrawableRes int resourceId) {
        mOptions = mOptions.fallback(resourceId);
        return this;
    }

    /**
     * Set Image Loading PlaceHolder
     *
     * @param resourceId Resource Id
     * @return GlideUtil
     */
    public GlideUtil placeHolder(@DrawableRes int resourceId) {
        mOptions = mOptions.placeholder(resourceId);
        return this;
    }

    /**
     * Image Fail Func.
     *
     * @param id Drawable Id
     * @return GlideUtil
     */
    public GlideUtil failDrawable(@DrawableRes int id) {
        mOptions = mOptions.error(id);
        return this;
    }

    /**
     * Image Fail Color
     *
     * @param id Color Id
     * @return GlideUtil
     */
    public GlideUtil failColor(@ColorRes int id) {
        mOptions = mOptions.error(id);
        return this;
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

    /**
     * Image Transforms Func.
     * 로딩한 이미지를 편집할수 있는 함수.
     *
     * @param transformations Round, Corner, Scale...
     * @return GlideUtil
     */
    public GlideUtil transforms(@NonNull Transformation<Bitmap> transformations) {
        mOptions = mOptions.transform(transformations);
        return this;
    }

    /**
     * Image Corner Func.
     *
     * @param ctx            Context
     * @param cornerRadiusId Corner Size
     * @return GlideUtil
     */
    public GlideUtil corner(
            @NonNull Context ctx,
            @DimenRes int cornerRadiusId) {
        return corner(ctx, BaseTransformation.ALL, cornerRadiusId);
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
    public GlideUtil corner(
            @NonNull Context ctx,
            @BaseTransformation.CornerType int type,
            @DimenRes int cornerRadiusId) {
        return corner(type, ctx.getResources().getDimensionPixelOffset(cornerRadiusId));
    }

    /**
     * Image Corner Func.
     *
     * @param type         TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
     * @param cornerRadius Corner Radius
     * @return GlideUtil
     */
    public GlideUtil corner(
            @BaseTransformation.CornerType int type,
            int cornerRadius
    ) {
        if (mScaleType != null) {
            throw new IllegalStateException("transform 사용시 ScaleType 을 설정하면 안됩니다.");
        }
        mOptions = mOptions.transform(new MultiTransformation<>(
                new CenterCrop(),
                new CornerTransformation(type).corner(cornerRadius)
        ));
        return this;
    }

    /**
     * Image Border Func.
     *
     * @param ctx           Context
     * @param borderWidthId Border Width Id
     * @param borderColorId Border Color Id
     * @return GlideUtil
     */
    public GlideUtil border(
            @NonNull Context ctx,
            @DimenRes int borderWidthId,
            @ColorRes int borderColorId
    ) {
        return border(ctx.getResources().getDimensionPixelOffset(borderWidthId),
                ContextCompat.getColor(ctx, borderColorId));
    }

    /**
     * Image Border Func
     *
     * @param borderWidth Border Width
     * @param borderColor Border Color
     * @return GlideUtil
     */
    public GlideUtil border(int borderWidth, int borderColor) {
        if (mScaleType != null) {
            throw new IllegalStateException("transform 사용시 ScaleType 을 설정하면 안됩니다.");
        }
        mOptions = mOptions.transform(new MultiTransformation<>(
                new CenterCrop(),
                new BorderTransformation().border(borderWidth, borderColor)
        ));
        return this;
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
    public GlideUtil cornerAndBorder(@NonNull Context ctx,
                                     @DimenRes int cornerRadiusId,
                                     @DimenRes int borderWidthId,
                                     @ColorRes int borderColorId) {
        return cornerAndBorder(ctx, CornerTransformation.ALL, cornerRadiusId, borderWidthId, borderColorId);
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
    public GlideUtil cornerAndBorder(@NonNull Context ctx,
                                     @CornerTransformation.CornerType int type,
                                     @DimenRes int radiusId,
                                     @DimenRes int borderWidthId,
                                     @ColorRes int borderColorId) {
        return cornerAndBorder(
                type,
                ctx.getResources().getDimensionPixelOffset(radiusId),
                ctx.getResources().getDimensionPixelOffset(borderWidthId),
                ContextCompat.getColor(ctx, borderColorId));
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
    public GlideUtil cornerAndBorder(@CornerTransformation.CornerType int type,
                                     int cornerRadius,
                                     int borderWidth,
                                     int borderColor) {
        if (mScaleType != null) {
            throw new IllegalStateException("transform 사용시 ScaleType 을 설정하면 안됩니다.");
        }

        mOptions = mOptions.transform(
                new MultiTransformation<>(new CenterCrop(),
                        new CornerTransformation(type)
                                .corner(cornerRadius)
                                .border(borderWidth, borderColor)
                )
        );
        return this;
    }


    /**
     * 캐시된 메모리 사용하지 않고
     * 계속해서 이미지 로드해서 사용하는 함수.
     *
     * @return GlideUtil
     */
    public GlideUtil skinCached() {
        mOptions = mOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        mOptions = mOptions.skipMemoryCache(true);
        return this;
    }

    public void into(AppCompatImageView imgView) {
        into(imgView, null);
    }

    /**
     * 이미지 로딩 시작 함수.
     *
     * @param imgView Show ImageView
     */
    public void into(AppCompatImageView imgView, @Nullable final Listener listener) {
        Activity act = getActivity(imgView);
        // java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
        // 방어 코드
        if (act == null || act.isFinishing()) return;
        // 이미지 로딩 경로 유효성 검사
        if (mUri == null && mResId == -1) return;

        // 이미지 타입에 따라서 ImageView 타입 수정.
        if (mScaleType != null) {
            switch (mScaleType) {
                case CENTER_CROP:
                    imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case FIT_XY:
                    imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case CENTER_INSIDE:
                    imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
            }
        }

        Glide.with(imgView)
                .load((mUri != null) ? mUri : mResId)
                .apply(mOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null && e != null) {
                            listener.onFailed(e.getMessage());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null && resource != null) {
                            listener.onResource(resource);
                        }
                        return false;
                    }
                })
                .into(imgView);
    }

    /**
     * Cast View to Activity
     *
     * @param view View
     * @return Activity
     */
    private Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Clear Cache And Disk Memory
     *
     * @param context Context
     */
    public void clearCache(final Context context) {
        Flowable.just(clearMemory(context))
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    private boolean clearMemory(Context context) {
        Glide.get(context).clearDiskCache();
        Glide.get(context).clearMemory();
        return true;
    }
}
