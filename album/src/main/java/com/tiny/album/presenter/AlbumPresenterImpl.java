package com.tiny.album.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.tiny.album.bean.Gallery;
import com.tiny.album.model.AlbumModelImpl;
import com.tiny.album.model.IAlbumModel;
import com.tiny.album.presenter.entity.ImageScanResult;
import com.tiny.album.view.IAlbumView;

import java.util.List;

/**
 * 功能描述：相册presenter
 * Created by pumengxia on 2018/5/24 0024 下午 5:46.
 */

public class AlbumPresenterImpl implements IAlbumPresenter {
    private IAlbumView mAlbumView;
    private IAlbumModel mAlbumModel;

    public AlbumPresenterImpl() {
    }

    @Override
    public void attach(IAlbumView view) {
        mAlbumView = view;
        mAlbumModel = new AlbumModelImpl();
    }

    @Override
    public void startScanImage(final Context context, LoaderManager loaderManager) {
        mAlbumModel.startScanImage(context, loaderManager, new IAlbumModel.OnScanImageFinish() {
            @Override
            public void onFinish() {
                List<Gallery> galleries = ImageScanResult.getmGalleries();
                if (galleries.size()>0){
                    mAlbumView.refreshAlbumData(galleries);
                }
            }
        });
    }

    @Override
    public List<Gallery> getGalleries(){
        return ImageScanResult.getmGalleries();
    }

    @Override
    public void detach() {
        mAlbumView = null;
    }

}
