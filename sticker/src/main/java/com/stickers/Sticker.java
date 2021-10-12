package com.stickers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static java.lang.Math.round;

public abstract class Sticker {

    @IntDef(flag = true, value = {
            Position.CENTER, Position.TOP, Position.BOTTOM, Position.LEFT, Position.RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
        int CENTER = 1;
        int TOP = 1 << 1;
        int LEFT = 1 << 2;
        int RIGHT = 1 << 3;
        int BOTTOM = 1 << 4;
    }

    private final float[] matrixValues = new float[9];
    private final float[] unRotatedWrapperCorner = new float[8];
    private final float[] unRotatedPoint = new float[2];
    private final float[] boundPoints = new float[8];
    private final float[] mappedBounds = new float[8];
    private final RectF trappedRect = new RectF();
    private final Matrix matrix = new Matrix();
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;

    public boolean isFlippedHorizontally() {
        return isFlippedHorizontally;
    }

    public void setFlippedHorizontally(boolean flippedHorizontally) {
        isFlippedHorizontally = flippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return isFlippedVertically;
    }

    public void setFlippedVertically(boolean flippedVertically) {
        isFlippedVertically = flippedVertically;
    }

    @NonNull
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(@Nullable Matrix matrix) {
        this.matrix.set(matrix);
    }

    public abstract void draw(@NonNull Canvas canvas);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract Sticker setDrawable(@NonNull Drawable drawable);

    @NonNull
    public abstract Drawable getDrawable();

    @NonNull
    public abstract Sticker setAlpha(@IntRange(from = 0, to = 255) int alpha);

    public void getBoundPoints(@NonNull float[] points) {
        if (!isFlippedHorizontally) {
            if (!isFlippedVertically) {
                points[0] = 0f;
                points[1] = 0f;
                points[2] = getWidth();
                points[3] = 0f;
                points[4] = 0f;
                points[5] = getHeight();
                points[6] = getWidth();
                points[7] = getHeight();
            } else {
                points[0] = 0f;
                points[1] = getHeight();
                points[2] = getWidth();
                points[3] = getHeight();
                points[4] = 0f;
                points[5] = 0f;
                points[6] = getWidth();
                points[7] = 0f;
            }
        } else {
            if (!isFlippedVertically) {
                points[0] = getWidth();
                points[1] = 0f;
                points[2] = 0f;
                points[3] = 0f;
                points[4] = getWidth();
                points[5] = getHeight();
                points[6] = 0f;
                points[7] = getHeight();
            } else {
                points[0] = getWidth();
                points[1] = getHeight();
                points[2] = 0f;
                points[3] = getHeight();
                points[4] = getWidth();
                points[5] = 0f;
                points[6] = 0f;
                points[7] = 0f;
            }
        }
    }

    public void getMappedPoints(@NonNull float[] dst, @NonNull float[] src) {
        matrix.mapPoints(dst, src);
    }

    public void getCenterPoint(@NonNull PointF dst) {
        dst.set(getWidth() * 1f / 2, getHeight() * 1f / 2);
    }

    public void getMappedCenterPoint(@NonNull PointF dst, @NonNull float[] mappedPoints,
                                     @NonNull float[] src) {
        getCenterPoint(dst);
        src[0] = dst.x;
        src[1] = dst.y;
        getMappedPoints(mappedPoints, src);
        dst.set(mappedPoints[0], mappedPoints[1]);
    }

    /**
     * @return - current image rotation angle.
     */
    public float getCurrentAngle() {
        return getMatrixAngle(matrix);
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) Math.toDegrees(-(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X))));
    }

    public float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = 9) int valueIndex) {
        matrix.getValues(matrixValues);
        return matrixValues[valueIndex];
    }

    public boolean contains(@NonNull float[] point) {
        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-getCurrentAngle());
        getBoundPoints(boundPoints);
        getMappedPoints(mappedBounds, boundPoints);
        tempMatrix.mapPoints(unRotatedWrapperCorner, mappedBounds);
        tempMatrix.mapPoints(unRotatedPoint, point);
        trapToRect(trappedRect, unRotatedWrapperCorner);
        return trappedRect.contains(unRotatedPoint[0], unRotatedPoint[1]);
    }

    public static void trapToRect(@NonNull RectF r, @NonNull float[] array) {
        r.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
                Float.NEGATIVE_INFINITY);
        for (int i = 1; i < array.length; i += 2) {
            float x = round(array[i - 1] * 10) / 10.f;
            float y = round(array[i] * 10) / 10.f;
            r.left = Math.min(x, r.left);
            r.top = Math.min(y, r.top);
            r.right = Math.max(x, r.right);
            r.bottom = Math.max(y, r.bottom);
        }
        r.sort();
    }

    public void release() {
    }
}
