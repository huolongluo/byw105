package com.legend.modular_contract_sdk.component.image_loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.legend.modular_contract_sdk.R;


public class ImageLoader {


    public static void loadLocalPath(ImageView imageView, String path) {
        getCommonBuilder(imageView.getContext(), path)
                .into(imageView);
    }

    public static void loadLocalUri(ImageView imageView, Uri uri) {
        getCommonBuilder(imageView.getContext(), uri)
                .into(imageView);
    }

    public static RequestBuilder<Drawable> getCommonBuilder(Context context, Object source) {
        return Glide.with(context)
                .load(source)
                .apply(RequestOptions.placeholderOf(R.drawable.mc_sdk_img_placeholder))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL));
    }
}
