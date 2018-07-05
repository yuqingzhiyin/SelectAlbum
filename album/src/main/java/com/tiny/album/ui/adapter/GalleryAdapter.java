package com.tiny.album.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tiny.album.R;
import com.tiny.album.bean.Photo;
import com.tiny.album.imageloader.ImageLoaderFactory;
import com.tiny.album.imageloader.ImageLoaderWrapper;
import com.tiny.album.ui.holder.CameraHolder;
import com.tiny.album.ui.holder.GridPhotoHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Created by pumengxia on 2018/4/26 0026 下午 5:24.
 */

public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoaderWrapper mImageLoaderWrapper;

    private boolean mIsShowCamera;
    private IGalleryListener mGalleryListener;
    private List<Photo> mPhotos = new ArrayList<>();

    public GalleryAdapter(Context context,ImageLoaderWrapper loaderWrapper) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoaderWrapper = loaderWrapper;
    }

    public void setData(Map<Integer,Photo> photos){
        mPhotos.clear();
        for (Photo value : photos.values()) {
            mPhotos.add(value);
        }
    }

    public void setIsShowCamera(boolean showCamera){
        mIsShowCamera = showCamera;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA){
            return new CameraHolder(mInflater.inflate(R.layout.item_album_camera, parent, false));
        }else {
            return new GridPhotoHolder(mInflater.inflate(R.layout.item_album, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mIsShowCamera&&position == 0){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGalleryListener!=null){
                        mGalleryListener.clickCamera();
                    }
                }
            });
            return;
        }
        if (mIsShowCamera){
            if (position>mPhotos.size()){
                return;
            }
            bindPos((GridPhotoHolder) holder,position-1);
        }else {
            if (position>=mPhotos.size()){
                return;
            }
            bindPos((GridPhotoHolder) holder,position);
        }

    }

    private void bindPos(GridPhotoHolder holder,int position){
        Photo photo = mPhotos.get(position);
        ImageLoaderWrapper.DisplayOption option = new ImageLoaderWrapper.DisplayOption();
        option.loadErrorResId = R.drawable.img_error;
        option.loadingResId = R.drawable.img_default;
        mImageLoaderWrapper.displayImage(holder.mImage,photo.path,option);
        boolean select = photo.isSelect;
        holder.mSelectView.setSelected(select);
        holder.mSelectView.setTag(photo);
        holder.mSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = (Photo) v.getTag();
                boolean select = photo.isSelect;
                if (mGalleryListener!=null&&mGalleryListener.selectPhoto(photo)){
                    v.setSelected(!select);
                }

            }
        });
        holder.itemView.setTag(photo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = (Photo) v.getTag();
                if (mGalleryListener!=null){
                    mGalleryListener.goPreview(photo);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0&&mIsShowCamera){
            return TYPE_CAMERA;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mIsShowCamera?mPhotos.size()+1:mPhotos.size();
    }

    public void setGalleryListener(IGalleryListener gallerListener){
        mGalleryListener = gallerListener;
    }

    public interface IGalleryListener{
        void clickCamera();
        boolean selectPhoto(Photo photo);
        void goPreview(Photo photo);
    }
}
