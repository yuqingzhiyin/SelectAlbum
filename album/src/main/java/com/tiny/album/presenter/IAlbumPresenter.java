package com.tiny.album.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.tiny.album.bean.Gallery;
import com.tiny.album.view.IAlbumView;

import java.util.List;

/**
 * 功能描述：
 * Created by pumengxia on 2018/5/24 0024 下午 5:46.
 */

public interface IAlbumPresenter{

    void attach(IAlbumView view);

    void detach();

    void startScanImage(Context context, LoaderManager loaderManager);

    List<Gallery> getGalleries();
}
