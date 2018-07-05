package com.tiny.album.imageloader;

/**
 * 功能描述：图片加载工厂类
 * Created by pumengxia on 2018/5/29 0029 上午 11:19.
 */

public class ImageLoaderFactory {
    private static volatile ImageLoaderWrapper sInstance;

    private ImageLoaderFactory() {

    }

    /**
     * 获取图片加载器
     *
     * @return
     */
    public static ImageLoaderWrapper getLoader() {
        if (sInstance == null) {
            synchronized (ImageLoaderFactory.class) {
                if (sInstance == null) {
                    sInstance = new GlideImageLoader();
                }
            }
        }
        return sInstance;
    }
}
