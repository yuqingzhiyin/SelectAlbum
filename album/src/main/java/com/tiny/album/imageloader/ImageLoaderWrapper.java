package com.tiny.album.imageloader;

import android.widget.ImageView;

/**
 * 功能描述：图片加载功能接口
 * Created by pumengxia on 2018/5/29 0029 上午 11:08.
 */

public interface ImageLoaderWrapper {

    void displayImage(ImageView imageView, String path, DisplayOption option);
    void displayImage(ImageView imageView, String path);

    class DisplayOption {
        /**
         * 加载中的资源id
         */
        public int loadingResId;
        /**
         * 加载失败的资源id
         */
        public int loadErrorResId;
    }
}
