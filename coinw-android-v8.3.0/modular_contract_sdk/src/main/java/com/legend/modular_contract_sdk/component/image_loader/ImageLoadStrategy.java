package com.legend.modular_contract_sdk.component.image_loader;

/**
 * Created by Spencer on 8/25/18.
 */
public interface ImageLoadStrategy {

    int DEFAULT_QUALITY = 90;

    String JPG_FORMAT = "jpg";

    String WEBP_FORMAT = "webp";

    String PNG_FORMAT = "png";

    String DEFAULT_FORMAT = WEBP_FORMAT;

    int getWidth();

    int getHeight();

    int getQuality();

    String getFormat();

    String formatUrl(String url);
}
