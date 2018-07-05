package com.tiny.album.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tiny.album.R;

/**
 * 功能描述：相册照片holder
 * Created by pumengxia on 2018/5/28 0028 下午 4:34.
 */

public class GridPhotoHolder extends RecyclerView.ViewHolder{
    public ImageView mImage;
    public ImageView mSelectView;
    public GridPhotoHolder(View itemView) {
        super(itemView);
        mImage = itemView.findViewById(R.id.iv_photo);
        mSelectView = itemView.findViewById(R.id.ckb_image_select);;
    }
}
