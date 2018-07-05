package com.tiny.album.presenter.entity;

import com.tiny.album.bean.Gallery;
import com.tiny.album.bean.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 功能描述：图片扫描结果
 * Created by pumengxia on 2018/5/29 0029 下午 1:45.
 */

public class ImageScanResult {
    private static List<Gallery> mGalleries = new Vector<>();
    private static ArrayList<String> mSelectPhotos = new ArrayList<>();

    public static ArrayList<String> getSelectPhotos() {
        return mSelectPhotos;
    }

    public static List<Gallery> getmGalleries() {
        return mGalleries;
    }

    public static void setGalleries(List<Gallery> galleries) {
        mGalleries.clear();
        mGalleries.addAll(galleries);
    }

    public static void reset() {
        mGalleries.clear();
        mSelectPhotos.clear();
    }
}
