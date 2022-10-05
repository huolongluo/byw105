package com.android.legend.util.glide;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
/**
 * glide加载图片时，以控件宽度为准，调整高度，保证正常的图片的宽高比显示
 */
public class TransformationUtils extends ImageViewTarget<Bitmap> {

    private ImageView target;

    public TransformationUtils(ImageView target) {
        super(target);
        this.target = target;
    }

    @Override
    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);

        //获取原图的宽高
        int width = resource.getWidth();
        int height = resource.getHeight();

        //获取imageView的宽
        int imageViewWidth = target.getWidth();

        //计算缩放比例
        float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

        //计算图片等比例放大后的高
        int imageViewHeight = (int) (height * sy);
        ViewGroup.LayoutParams params = target.getLayoutParams();
        params.height = imageViewHeight;
        target.setLayoutParams(params);
    }
}
