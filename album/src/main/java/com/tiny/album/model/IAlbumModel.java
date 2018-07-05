package com.tiny.album.model;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.tiny.album.bean.Gallery;

import java.util.List;

/**
 * 功能描述：相册读取model接口
 * Created by pumengxia on 2018/5/24 0024 下午 6:07.
 */

public interface IAlbumModel {
    void startScanImage(Context context, LoaderManager loaderManager, OnScanImageFinish onScanImageFinish);

    interface OnScanImageFinish {
        /**
         * 扫描结束的时候执行此函数
         */
        void onFinish();

    }
}
