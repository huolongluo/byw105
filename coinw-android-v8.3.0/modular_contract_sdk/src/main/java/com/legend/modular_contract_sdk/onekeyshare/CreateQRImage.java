package com.legend.modular_contract_sdk.onekeyshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.Hashtable;

/**
 * 通过ZXing创建二维码图片
 */
public class CreateQRImage
{
    public Bitmap createQRImage(String url, final int width, final int height,boolean isTran)
    {
        try
        {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * width + x] = 0xff000000;
                    }
                    else
                    {
                        if(isTran){
                            pixels[y * width + x] = 0x00ffffff;
                        }else {
                            pixels[y * width + x] = 0xffffffff;
                        }

                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成带logo的二维码，logo默认为二维码的1/5
     *
     * @param text    需要生成二维码的文字、网址等
     * @param size    需要生成二维码的大小（）
     * @param mBitmap logo文件
     * @return bitmap
     */
    public static Bitmap createQRCodeWithLogo(String text, int size, Bitmap mBitmap) {
        return createQRCodeWithLogo(text, size, mBitmap, 0);
    }

    public static Bitmap createQRCodeWithLogo(String text, int size, Bitmap mBitmap, int spaceWidth) {
        try {
            int IMAGE_HALFWIDTH = size / 10;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            /*
             * 设置容错级别，默认为ErrorCorrectionLevel.L
             * 因为中间加入logo所以建议你把容错级别调至H,否则可能会出现识别不了
             */
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, spaceWidth); //default is 4
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);

            int width = bitMatrix.getWidth();//矩阵高度
            int height = bitMatrix.getHeight();//矩阵宽度
            int halfW = width / 2;
            int halfH = height / 2;

            Matrix m = new Matrix();
            float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
            float sy = (float) 2 * IMAGE_HALFWIDTH
                    / mBitmap.getHeight();
            m.setScale(sx, sy);
            //设置缩放信息
            //将logo图片按martix设置的信息缩放
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                    mBitmap.getWidth(), mBitmap.getHeight(), m, false);

            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
                            && y > halfH - IMAGE_HALFWIDTH
                            && y < halfH + IMAGE_HALFWIDTH) {
                        //该位置用于存放图片信息
                        //记录图片每个像素信息
                        pixels[y * width + x] = mBitmap.getPixel(x - halfW
                                + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * size + x] = 0xff000000;
                        } else {
                            pixels[y * size + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
        return resMatrix;
    }


    public static Bitmap creatBarcode(Context context, String contents, int desiredWidth, int desiredHeight, boolean displayCode)
    {
        Bitmap ruseltBitmap = null;
        /**
         * 图片两端所保留的空白的宽度
         */
        int marginW = 0;
        /**
         * 条形码的编码类型
         */
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode)
        {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2 * marginW, desiredHeight, context);
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
        }
        else
        {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    protected static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight)
    {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try
        {
            result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
        }
        catch (WriterException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++)
        {
            int offset = y * width;
            for (int x = 0; x < width; x++)
            {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    protected static Bitmap creatCodeBitmap(String contents, int width, int height, Context context)
    {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first
     * @param second
     * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint)
    {
        if (first == null || second == null || fromPoint == null)
        {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth() + second.getWidth() + marginW, first.getHeight() + second.getHeight(), Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save();
//        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }
}
