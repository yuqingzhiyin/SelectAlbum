package com.tiny.album.presenter;

import android.os.AsyncTask;

import com.tiny.album.bean.Gallery;
import com.tiny.album.bean.Photo;
import com.tiny.album.presenter.entity.ImageScanResult;
import com.tiny.album.view.IPreviewView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * 功能描述：
 * Created by pumengxia on 2018/5/29 0029 下午 2:40.
 */

public class PreviewPresenterImpl implements IPreviewPresenter {
    private IPreviewView mView;
    private List<Photo> mPhotos = new Vector<>();
    @Override
    public void attach(IPreviewView view) {
        mView = view;
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public void getNowPhotos(final int albumId, final Photo photo) {
        new AsyncTask<Integer,Integer,Integer>(){
            @Override
            protected Integer doInBackground(Integer... integers) {
                List<Gallery> galleries = ImageScanResult.getmGalleries();

                for (Gallery gallery : galleries) {
                    if (gallery.galleryId == integers[0]){
                        mPhotos.clear();
                        List<Photo> photoList = getPhotos(gallery.mPhotos);
                        mPhotos.addAll(photoList);
                        break;
                    }
                }
                if (mPhotos.size()<1){
                    return -1;
                }
                for (int i = 0;i<mPhotos.size();i++){
                    if (mPhotos.get(i).equals(photo)){
                        return i;
                    }
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer position) {
                super.onPostExecute(position);
                if (position>=0) {
                    mView.selectPos(position);
                }
            }
        }.execute(albumId);
    }

    @Override
    public List<Photo> getPhotos() {
        return mPhotos;
    }

    private List<Photo> getPhotos(TreeMap<Integer,Photo> photos){
        List<Photo> photoList = new ArrayList<>();
        for (Photo value : photos.values()) {
            photoList.add(value);
        }
        return photoList;
    }
}
