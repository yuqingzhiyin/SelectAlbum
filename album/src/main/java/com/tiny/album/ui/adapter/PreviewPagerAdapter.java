package com.tiny.album.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.tiny.album.R;
import com.tiny.album.bean.Photo;
import com.tiny.album.imageloader.ImageLoaderWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：图片预览ViewPagerAdapter
 * Created by pumengxia on 2018/5/28 0028 下午 5:57.
 */

public class PreviewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<Photo> mPhotos = new ArrayList<>();
    private ImageLoaderWrapper mLoaderWrapper;
    private IPagerListener mPagerListener;

    public PreviewPagerAdapter(Context context,ImageLoaderWrapper loaderWrapper,IPagerListener pagerListener) {
        mContext = context;
        mLoaderWrapper = loaderWrapper;
        mPagerListener = pagerListener;
    }

    public void setData(List<Photo> photos){
        mPhotos.clear();
        mPhotos.addAll(photos);
    }

    @Override
    public int getCount() {
        if (mPhotos == null) {
            return 0;
        }
        return mPhotos.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View galleryItemView = View.inflate(mContext, R.layout.item_pager_preview, null);

        Photo imageInfo = mPhotos.get(position);
        PhotoView galleryPhotoView =  galleryItemView.findViewById(R.id.iv_show_image);
        mLoaderWrapper.displayImage(galleryPhotoView, imageInfo.path);
        galleryPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPagerListener!=null){
                    mPagerListener.toggleImmersiveMode();
                }
            }
        });

        container.addView(galleryItemView);
        return galleryItemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        PhotoView galleryPhotoView =  view.findViewById(R.id.iv_show_image);
        galleryPhotoView.setScale(1);
        return view == object;
    }

    public interface IPagerListener{
        void toggleImmersiveMode();
    }
}
