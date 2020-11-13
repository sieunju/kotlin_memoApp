package com.hmju.memo.widget.glide.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.security.MessageDigest;

/**
 * Description: Glide Corner And Border Class
 * <p>
 * Created by hmju on 2020-11-09
 */
public class CornerTransformation extends BaseTransformation {

    private static final String ID = "com.hmju.memo.widget.glide.transformation.CornerTransformation";

    public int mCornerRadius = 0;
    public int mBorderWidth = -1;
    public int mBorderColor = -1;

    public CornerTransformation(
            @CornerType int type) {
        super(type);
    }

    /**
     * set Corner Radius
     *
     * @param cornerRadius Corner Radius
     * @return CornerTransformation
     */
    public CornerTransformation corner(int cornerRadius) {
        mCornerRadius = cornerRadius;
        return this;
    }

    /**
     * set Border Setting
     *
     * @param borderWidth Border Width
     * @param borderColor Border Color
     * @return CornerTransformation
     */
    public CornerTransformation border(int borderWidth, int borderColor) {
        mBorderWidth = borderWidth;
        mBorderColor = borderColor;
        return this;
    }
    
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = pool.get(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        setCanvasBitmapDensity(toTransform, bitmap);

        // Draw Round Rect
        drawRoundRect(bitmap, toTransform);

        // Draw Border
        drawBorder(bitmap);

        return bitmap;
    }

    /**
     * Draw Round Rect Func..
     *
     * @param bitmap      Current Bitmap
     * @param toTransform Source Bitmap
     */
    private void drawRoundRect(@NonNull Bitmap bitmap, @NonNull Bitmap toTransform) {
        if (mCornerRadius == 0) return;

        int right = toTransform.getWidth();
        int bottom = toTransform.getHeight();

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        RectF rect = new RectF();

        // Type All
        if ((mType & ALL) == ALL) {
            rect.set(0, 0, right, bottom);
            canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, paint);
        } else {

            int diameter = mCornerRadius * 2; // 기울기 길이

            if ((mType & TOP_LEFT) == TOP_LEFT) {
                rect.set(0, 0, diameter, diameter);
                canvas.drawArc(rect, 180, 90, true, paint); // ┌─
            } else {
                rect.set(0, 0, mCornerRadius, mCornerRadius);
                canvas.drawRect(rect, paint); // Top Left ■
            }

            if ((mType & TOP_RIGHT) == TOP_RIGHT) {
                rect.set(right - diameter, 0, right, diameter);
                canvas.drawArc(rect, 270, 90, true, paint); // ─┐
            } else {
                rect.set(right - mCornerRadius, 0, right, mCornerRadius);
                canvas.drawRect(rect, paint); // Top Right ■
            }

            rect.set(mCornerRadius, 0, right - mCornerRadius, (float) diameter / 2);
            canvas.drawRect(rect, paint); // Top ──

            if ((mType & BOTTOM_LEFT) == BOTTOM_LEFT) {
                rect.set(0, bottom - diameter, diameter, bottom);
                canvas.drawArc(rect, 90, 90, true, paint); // └─
            } else {
                rect.set(0, bottom - mCornerRadius, mCornerRadius, bottom);
                canvas.drawRect(rect, paint); // Bottom Left ■
            }

            if ((mType & BOTTOM_RIGHT) == BOTTOM_RIGHT) {
                rect.set(right - diameter, bottom - diameter, right, bottom);
                canvas.drawArc(rect, 0, 90, true, paint); // ─┘
            } else {
                rect.set(right - mCornerRadius, bottom - mCornerRadius, right, bottom);
                canvas.drawRect(rect, paint); // Bottom Right ■
            }

            rect.set(mCornerRadius, bottom - mCornerRadius, toTransform.getWidth() - mCornerRadius, bottom);
            canvas.drawRect(rect, paint); // Bottom ──

            // Body Rect
            rect.set(0, mCornerRadius, right, bottom - mCornerRadius);
            canvas.drawRect(rect, paint); // Body Rect
        }

        clear(canvas);
    }

    /**
     * Draw Border
     *
     * @param toTransform Source Bitmap
     */
    private void drawBorder(@NonNull Bitmap toTransform) {
        if (mBorderWidth == -1) return;

        Canvas canvas = new Canvas(toTransform);
        Path path = new Path();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mBorderColor);

        RectF rect = new RectF();
        int right = toTransform.getWidth();
        int bottom = toTransform.getHeight();

        // Type All
        if ((mType & ALL) == ALL) {
            paint.setStyle(Paint.Style.FILL);

            // Draw Inner Rect
            rect.set(mBorderWidth, mBorderWidth, right - mBorderWidth, bottom - mBorderWidth);
            float innerRadius = mCornerRadius - (mBorderWidth / 2F);
            path.addRoundRect(
                    rect,
                    innerRadius,
                    innerRadius,
                    Path.Direction.CCW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipOutPath(path);
            } else {
                canvas.clipPath(path, Region.Op.DIFFERENCE);
            }

            // Draw Out Rect
            rect.set(0, 0, right, bottom);
            path.rewind();
            path.addRoundRect(
                    rect,
                    mCornerRadius,
                    mCornerRadius,
                    Path.Direction.CCW
            );

        } else {
            // Other Type.
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mBorderWidth);
            float lineMiddle = mBorderWidth / 2F;

            // Start Path
            path.moveTo(lineMiddle, mCornerRadius);

            // Round Top Left
            if ((mType & TOP_LEFT) == TOP_LEFT) {
                path.quadTo(
                        lineMiddle,
                        lineMiddle,
                        mCornerRadius,
                        lineMiddle);
            } else {
                path.lineTo(lineMiddle, lineMiddle);
                path.lineTo(mCornerRadius, lineMiddle);
            }

            // Top Line
            path.lineTo(right - mCornerRadius, lineMiddle);

            // Round Top Right
            if ((mType & TOP_RIGHT) == TOP_RIGHT) {
                path.quadTo(
                        right - lineMiddle,
                        lineMiddle,
                        right - lineMiddle,
                        mCornerRadius);
            } else {
                path.lineTo(right - lineMiddle, lineMiddle);
                path.lineTo(right - lineMiddle, mCornerRadius);
            }

            // Right Line
            path.lineTo(right - lineMiddle, bottom - mCornerRadius);

            // Round Bottom Right
            if ((mType & BOTTOM_RIGHT) == BOTTOM_RIGHT) {
                path.quadTo(
                        right - lineMiddle,
                        bottom - lineMiddle,
                        right - mCornerRadius,
                        bottom - lineMiddle);
            } else {
                path.lineTo(right - lineMiddle, bottom - lineMiddle);
                path.lineTo(right - mCornerRadius, bottom - lineMiddle);
            }

            // Bottom Line;
            path.lineTo(mCornerRadius, bottom - lineMiddle);

            // Round Bottom Left
            if ((mType & BOTTOM_LEFT) == BOTTOM_LEFT) {
                path.quadTo(
                        lineMiddle,
                        bottom - lineMiddle,
                        lineMiddle,
                        bottom - mCornerRadius);
            } else {
                path.lineTo(lineMiddle, bottom - lineMiddle);
                path.lineTo(lineMiddle, bottom - mCornerRadius);
            }

            // Left Line
            path.lineTo(lineMiddle, mCornerRadius);
            path.close();

        }
        canvas.drawPath(path, paint);
        clear(canvas);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CornerTransformation) {
            CornerTransformation other = (CornerTransformation) obj;
            return mType == other.mType &&
                    mCornerRadius == other.mCornerRadius &&
                    mBorderWidth == other.mBorderWidth &&
                    mBorderColor == other.mBorderColor;
        }

        return false;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + mCornerRadius + mBorderWidth + mBorderColor).getBytes(CHARSET));
    }
}
