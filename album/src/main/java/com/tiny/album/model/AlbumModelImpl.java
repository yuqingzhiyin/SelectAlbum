package com.tiny.album.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.tiny.album.bean.Gallery;
import com.tiny.album.bean.Photo;
import com.tiny.album.presenter.entity.ImageScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：相册读取model
 * Created by pumengxia on 2018/5/24 0024 下午 6:08.
 */

public class AlbumModelImpl implements IAlbumModel{
    /**
     * Loader的唯一ID号
     */
    private final static int IMAGE_LOADER_ID = 1000;
    /**
     * 加载数据的映射
     */
    private static final String[] IMAGE_PROJECTION = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA, //绝对路径
            MediaStore.Images.Media.BUCKET_ID,//相册id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME//相册名
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_MODIFIED + " desc";
    private OnScanImageFinish mOnScanImageFinish;

    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IMAGE_LOADER_ID:
                    ImageScanResult.setGalleries((List<Gallery>)msg.obj);
                    if (mOnScanImageFinish != null) {
                        mOnScanImageFinish.onFinish();
                    }
                    break;
            }
        }
    };

    @Override
    public void startScanImage(final Context context, LoaderManager loaderManager, final OnScanImageFinish onScanImageFinish) {
        mOnScanImageFinish = onScanImageFinish;
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader imageCursorLoader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null, null, SORT_ORDER);
                return imageCursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data.getCount() == 0) {
                    if (onScanImageFinish != null) {
                        onScanImageFinish.onFinish();//无图片直接返回null
                    }

                } else {
                    int idColumn = data.getColumnIndex(MediaStore.Images.Media._ID);
                    int pathColumn = data.getColumnIndex(MediaStore.Images.Media.DATA);
                    int bucketIdColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    int bucketNameColumn = data.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    List<Gallery> galleries = new ArrayList<>();
                    boolean hasGallery = false;
                    if (!data.moveToFirst()) {
                        return;
                    }
                    do {
                        int id = data.getInt(idColumn);
                        String imagePath = data.getString(pathColumn);
                        File file = new File(imagePath);
                        if (!file.exists() || file.length() < 100 || TextUtils.isEmpty(imagePath))
                            continue;
                        int bucketId = data.getInt(bucketIdColumn);
                        String bucketName = data.getString(bucketNameColumn);
                        Photo photo = new Photo();
                        photo.path = imagePath;
                        for (Gallery gallery:galleries){
                            if (gallery.galleryId == bucketId){
                                gallery.mPhotos.put(id,photo);
                                hasGallery = true;
                                break;
                            }
                        }
                        if (hasGallery){
                            hasGallery = false;
                            continue;
                        }else {
                            Gallery gallery = new Gallery();
                            gallery.galleryId = bucketId;
                            gallery.galleryName = bucketName;
                            gallery.mPhotos.put(id,photo);
                            galleries.add(gallery);
                        }

                    } while (data.moveToNext());
                    sendMessage(IMAGE_LOADER_ID,galleries);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
        loaderManager.initLoader(IMAGE_LOADER_ID, null, loaderCallbacks);//初始化指定id的Loader

    }

    private void sendMessage(int what,Object object){
        Message message = mRefreshHandler.obtainMessage();
        message.obj = object;
        message.what = what;
        mRefreshHandler.sendMessage(message);
    }
}
