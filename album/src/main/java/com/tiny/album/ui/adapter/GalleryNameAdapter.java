package com.tiny.album.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tiny.album.R;
import com.tiny.album.bean.Gallery;

import java.util.List;


/**
 * 功能描述：相册名称adapter
 * Created by pumengxia on 2018/5/3 0003 下午 1:44.
 */

public class GalleryNameAdapter extends RecyclerView.Adapter<GalleryNameAdapter.ViewHolder>{
    private List<Gallery> mGalleyList;
    private Context mContext;
    private LayoutInflater mInflater;
    private IGalleryNameListener mListener;
    public GalleryNameAdapter(Context context, List<Gallery> galleryList, IGalleryNameListener listener) {
        mContext = context;
        mGalleyList = galleryList;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_album_pop,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       Gallery gallery = mGalleyList.get(position);
       holder.mTextView.setText(gallery.galleryName);
       holder.itemView.setTag(position);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int position = (int) v.getTag();
               Gallery data = mGalleyList.get(position);
               if (mListener!=null){
                   mListener.changeAlbum(data);
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return mGalleyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_item);
        }
    }

    public interface IGalleryNameListener{
        void changeAlbum(Gallery gallery);
    }
}
