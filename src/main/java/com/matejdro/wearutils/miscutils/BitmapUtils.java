package com.matejdro.wearutils.miscutils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapUtils {
    @Nullable

    public static Bitmap getBitmap(Context context, Parcelable parcelable) {
        if (parcelable instanceof Bitmap) {
            return (Bitmap) parcelable;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && parcelable instanceof Icon) {
            return getBitmapFromIcon(context, (Icon) parcelable);
        } else if (parcelable instanceof Drawable) {
            return getBitmap((Drawable) parcelable);
        } else {
            return null;
        }
    }

    public static Bitmap shrinkPreservingRatio(Bitmap original, int newWidth, int newHeight) {
        return shrinkPreservingRatio(original, newWidth, newHeight, true);
    }

    public static Bitmap shrinkPreservingRatio(Bitmap original, int newWidth, int newHeight, boolean filter) {
        if (original == null) {
            return null;
        }

        if (original.getWidth() <= newWidth && original.getHeight() <= newHeight) {
            return original;
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        if (newWidth / (float) originalWidth < newHeight / (float) originalHeight) {
            newHeight = originalHeight * newWidth / originalWidth;
        } else {
            newWidth = originalWidth * newHeight / originalHeight;
        }

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, filter);
    }

    public static Bitmap resizeAndCrop(Bitmap original, int newWidth, int newHeight, boolean filter) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        int originalNewWidth = newWidth;
        int originalNewHeight = newHeight;

        if (newWidth / (float) originalWidth < newHeight / (float) originalHeight) {
            newWidth = originalWidth * newHeight / originalHeight;
        } else {
            newHeight = originalHeight * newWidth / originalWidth;
        }

        if (original.getWidth() <= newWidth && original.getHeight() <= newHeight) {
            return original;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, newWidth, newHeight, filter);
        return Bitmap.createBitmap(scaledBitmap, (newWidth - originalNewWidth) / 2, (newHeight - originalNewHeight) / 2, originalNewWidth, originalNewHeight, null, filter);
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.M)
    public static Bitmap getBitmapFromIcon(Context context, @Nullable Icon icon) {
        if (icon == null) {
            return null;
        }

        return getBitmap(icon.loadDrawable(context));
    }

    @Nullable
    public static Drawable getDrawableFromUri(Context context, Uri uri) throws SecurityException {
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException | UnsupportedOperationException e) {
            // UnsupportedOperationException is sometimes thrown if image cannot be retrieved
            if (e.getMessage() != null && e.getMessage().contains("Permission denied")) {
                throw new SecurityException(e.getMessage());
            }

            return null;
        }

        return Drawable.createFromStream(inputStream, uri.toString());
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }

        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return shrinkPreservingRatio(bitmapDrawable.getBitmap(), width, height);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @Nullable
    public static byte[] serialize(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Nullable
    public static Bitmap deserialize(@Nullable byte[] serializedBitmap) {
        if (serializedBitmap == null) {
            return null;
        }

        return BitmapFactory.decodeByteArray(serializedBitmap, 0, serializedBitmap.length);
    }

}
