package com.hmju.memo.widget.glide.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description: BaseBitmap Transformation
 * <p>
 * Created by hmju on 2020-11-10
 */
public abstract class BaseTransformation extends BitmapTransformation {

    public static final int TOP_LEFT = 0x00000001;
    public static final int TOP_RIGHT = 1 << 1;
    public static final int BOTTOM_LEFT = 1 << 2;
    public static final int BOTTOM_RIGHT = 1 << 3;
    public static final int ALL = 1 << 4;

    @IntDef(flag = true, value = {
            TOP_LEFT,
            TOP_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_RIGHT,
            ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CornerType {
    }

    protected final @CornerType
    int mType;

    /**
     * Constructor
     *
     * @param type Corner Type
     */
    public BaseTransformation(@CornerType int type) {

        if ((type & TOP_LEFT) == TOP_LEFT &&
                (type & TOP_RIGHT) == TOP_RIGHT &&
                (type & BOTTOM_LEFT) == BOTTOM_LEFT &&
                (type & BOTTOM_RIGHT) == BOTTOM_RIGHT) {
            type = ALL;
        }
        mType = type;
    }

    protected void setCanvasBitmapDensity(@NonNull Bitmap toTransform, @NonNull Bitmap canvasBitmap) {
        canvasBitmap.setDensity(toTransform.getDensity());
    }

    // Avoids warnings in M+.
    protected void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }
}
