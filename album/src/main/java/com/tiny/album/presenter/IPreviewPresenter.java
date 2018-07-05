package com.tiny.album.presenter;

import com.tiny.album.bean.Photo;
import com.tiny.album.view.IPreviewView;

import java.util.List;

/**
 * 功能描述：预览presenter接口
 * Created by pumengxia on 2018/5/29 0029 下午 2:39.
 */

public interface IPreviewPresenter {
    void attach(IPreviewView view);

    void detach();

    void getNowPhotos(int albumId, Photo photo);

    List<Photo> getPhotos();
}
