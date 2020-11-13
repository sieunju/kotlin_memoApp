package com.hmju.memo.widget.glide.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.security.MessageDigest;

/**
 * Description: Glide Border Class
 * <p>
 * Created by hmju on 2020-11-11
 */
public class BorderTransformation extends BaseTransformation {

    private static final String ID = "com.widget.glide.transformation.BorderTransformation";

    public int mBorderWidth = -1;
    public int mBorderColor = -1;

    public BorderTransformation() {
        super(ALL);
    }

    /**
     * set Border Setting
     *
     * @param borderWidth Border Width
     * @param borderColor Border Color
     * @return CornerTransformation
     */
    public BorderTransformation border(int borderWidth, int borderColor) {
        mBorderWidth = borderWidth;
        mBorderColor = borderColor;
        return this;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        // Draw Border
        drawBorder(toTransform);

        return toTransform;
    }

    private void drawBorder(@NonNull Bitmap toTransform) {
        if (mBorderWidth == -1) return;

        Canvas canvas = new Canvas(toTransform);
        Path path = new Path();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mBorderColor);
        paint.setStyle(Paint.Style.FILL);

        RectF rect = new RectF();
        int right = toTransform.getWidth();
        int bottom = toTransform.getHeight();

        // Draw Inner Rect
        rect.set(mBorderWidth, mBorderWidth, right - mBorderWidth, bottom - mBorderWidth);
        path.addRect(rect, Path.Direction.CCW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.clipOutPath(path);
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE);
        }

        // Draw Out Rect
        rect.set(0, 0, right, bottom);
        path.rewind();
        path.addRect(
                rect,
                Path.Direction.CCW
        );
        canvas.drawPath(path, paint);
        clear(canvas);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CornerTransformation) {
            CornerTransformation other = (CornerTransformation) obj;
            return mType == other.mType &&
                    mBorderWidth == other.mBorderWidth &&
                    mBorderColor == other.mBorderColor;
        }

        return false;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + mBorderWidth + mBorderColor).getBytes(CHARSET));
    }

}
