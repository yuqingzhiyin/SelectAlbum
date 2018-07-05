package com.tiny.album.imageloader;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tiny.album.R;

/**
 * 功能描述：Glide图片加载
 * Created by pumengxia on 2018/5/29 0029 上午 11:11.
 */

public class GlideImageLoader implements ImageLoaderWrapper {
    @Override
    public void displayImage(ImageView imageView, String path, DisplayOption option) {
        int imageLoadingResId = R.drawable.img_default;
        int imageErrorResId = R.drawable.img_error;
        if (option != null) {
            imageLoadingResId = option.loadingResId;
            imageErrorResId = option.loadErrorResId;
        }
        Glide.with(imageView.getContext())
                .load(path)
                .error(imageErrorResId)
                .placeholder(imageLoadingResId)
                .dontAnimate()
                .into(imageView);
    }

    @Override
    public void displayImage(ImageView imageView, String path) {
        Glide.with(imageView.getContext())
                .load(path)
                .into(imageView);

    }
}
