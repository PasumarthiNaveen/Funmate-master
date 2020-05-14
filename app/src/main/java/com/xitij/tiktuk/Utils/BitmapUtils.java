package com.xitij.tiktuk.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

public class BitmapUtils {
    public Bitmap getRoundedBitmap(Bitmap source, int radius, int margin) {

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        float right = width - margin;
        float bottom = height - margin;
        canvas.drawRoundRect(new RectF(margin, margin, right, bottom), radius, radius, paint);
        source.recycle();

        return bitmap;
    }
}
