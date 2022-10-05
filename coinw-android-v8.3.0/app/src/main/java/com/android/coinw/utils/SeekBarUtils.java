package com.android.coinw.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.widget.SeekBar;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
public class SeekBarUtils {
    public static LayerDrawable blueLayerDrawable;
    public static LayerDrawable redLayerDrawable;

    public static void initBlueLayerDrawable(Context context, SeekBar seekBar, int width, int height) {
        if (context == null || seekBar == null) {
            Logger.getInstance().debug("SeekBarUtils", "context or seekbar is null.");
            return;
        }
        //
        if (blueLayerDrawable == null) {
            Drawable background = context.getResources().getDrawable(R.drawable.ic_seekbar_normal);
            Drawable secondaryProgress = context.getResources().getDrawable(R.drawable.ic_seekbar_pressed);
            //
            Drawable progressDrawable = seekBar.getProgressDrawable();
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
//            int width = layerDrawable.getDrawable(0).getIntrinsicWidth();
//            int height = layerDrawable.getDrawable(0).getIntrinsicHeight();
            //
            if (width == 0) {
                width = 547;
                height = 90;
            }
            Drawable[] ds = new Drawable[3];
            Drawable targetBackground = zoomDrawable(background, width, height);
            Drawable targetProgress = zoomForClipDrawable(secondaryProgress, width, height);
            ds[0] = targetBackground;
            ds[1] = targetProgress;
            ds[2] = targetProgress;
            blueLayerDrawable = new LayerDrawable(ds);
        }
        //
//        Drawable thumb = context.getResources().getDrawable(R.drawable.bg_1);
//        Drawable thumb2 = zoomDrawable2(thumb,55,90);
        //
        seekBar.setProgressDrawable(blueLayerDrawable);
        seekBar.setPadding(0, 0, 30, 0);
//        seekBar.setThumb(context.getResources().getDrawable(R.drawable.seekbar_tag));
//        seekBar.setThumb(thumb2);
        seekBar.setThumbOffset(0);
//        seekBar.setMax(100);
        //由于时间太紧张，图片在绘制时有偏，故采用偏移量来解决
        //seekbar的值范围为（1--101），所有计算的值，均做减1操作
        seekBar.setProgress(2);//滑动重置后可能会重叠
        seekBar.setProgress(1);
        //
    }

    public static void initRedLayerDrawable(Context context, SeekBar seekBar, int width, int height) {
        if (context == null || seekBar == null) {
            Logger.getInstance().debug("SeekBarUtils", "context or seekbar is null.");
            return;
        }
        //
        if (redLayerDrawable == null) {
            Drawable background = context.getResources().getDrawable(R.drawable.ic_seekbar_normal);
//            Drawable secondaryProgress = context.getResources().getDrawable(R.drawable.ic_seekbar);
            //
            Drawable progressDrawable = seekBar.getProgressDrawable();
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
//            int height = layerDrawable.getDrawable(0).getIntrinsicHeight();
//            int width = layerDrawable.getDrawable(0).getIntrinsicWidth();
            //
            Drawable[] ds = new Drawable[3];
            Drawable targetBackground = zoomDrawable(background, width, height);
//            Drawable targetProgress = zoomForClipDrawable(secondaryProgress, width, height);
//            ds[0] = targetBackground;
//            ds[1] = targetProgress;
//            ds[2] = targetProgress;
//            redLayerDrawable = new LayerDrawable(ds);
        }
        //
//        Drawable thumb = context.getResources().getDrawable(R.drawable.bg_2);
//        Drawable thumb2 = zoomDrawable2(thumb,55,90);
        //
//        seekBar.setProgressDrawable(redLayerDrawable);
//        seekBar.setPadding(0, 0, 0, 0);
//        seekBar.setThumb(context.getResources().getDrawable(R.drawable.bg_2));
////        seekBar.setThumb(thumb2);
//        seekBar.setThumbOffset(0);
////        seekBar.setMax(100);
//        //由于时间太紧张，由于图片在绘制时有偏，故采用偏移量来解决
//        //seekbar的值范围为（1--101），所有计算的值，均做减1操作
//        seekBar.setProgress(2);//滑动重置后可能会重叠
//        seekBar.setProgress(1);
        //
    }

    private static BitmapDrawable zoomDrawable(Drawable drawable, int w, int h) {
//        int width = drawable.getIntrinsicWidth();
//        int height = drawable.getIntrinsicHeight();
        int width = 547;
        int height = 90;
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

    private static BitmapDrawable zoomDrawable2(Drawable drawable, int w, int h) {
//        int width = drawable.getIntrinsicWidth();
//        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap2(drawable);
//        Matrix matrix = new Matrix();
//        float scaleWidth = ((float) w / width);
//        float scaleHeight = ((float) h / height);
//        matrix.postScale(1, 1);
//        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(null, oldbmp);
    }

    private static ClipDrawable zoomForClipDrawable(Drawable drawable, int w, int h) {
        BitmapDrawable bd = zoomDrawable(drawable, w, h);
        return new ClipDrawable(bd, Gravity.LEFT, ClipDrawable.HORIZONTAL);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
//        int width = drawable.getIntrinsicWidth();
//        int height = drawable.getIntrinsicHeight();
        int width = 547;
        int height = 90;
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap drawableToBitmap2(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(30, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
