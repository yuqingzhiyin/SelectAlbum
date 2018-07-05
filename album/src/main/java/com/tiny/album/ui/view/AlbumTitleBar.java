package com.tiny.album.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiny.album.R;

/**
 * 功能描述：相册titlebar
 * Created by pumengxia on 2018/5/3 0003 上午 10:44.
 */

public class AlbumTitleBar extends RelativeLayout{
    private TextView mGalleryNameText;
    private TextView mAheadBtn;
    private View mBackView;

    private OnClickListener mOnBackClickListener;
    private OnClickListener mOnAheadClickListener;
    private OnClickListener mOnGalleryClickListener;

    public AlbumTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.layout_album_titlebar, this);
        mGalleryNameText = findViewById(R.id.tv_title);
        mAheadBtn = findViewById(R.id.tv_go_ahead);
        mBackView = findViewById(R.id.iv_back);
        mGalleryNameText.setOnClickListener(mOnClickListener);
        mAheadBtn.setOnClickListener(mOnClickListener);
        mBackView.setOnClickListener(mOnClickListener);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.AlbumTitleBar);
        boolean showSelect = a.getBoolean(R.styleable.AlbumTitleBar_showSelect,true);
        String rightString = a.getString(R.styleable.AlbumTitleBar_rightText);
        if (!showSelect) {
            mGalleryNameText.setCompoundDrawables(null, null, null, null);// 设置到控件中
        }
        if (!TextUtils.isEmpty(rightString)) {
            mAheadBtn.setText(rightString);
        }
        a.recycle();
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_title){
                if (mOnGalleryClickListener!=null){
                    mOnGalleryClickListener.onClick(v);
                }
            }else if (id == R.id.tv_go_ahead){
                if (mOnAheadClickListener!=null){
                    mOnAheadClickListener.onClick(v);
                }
            }else if (id == R.id.iv_back){
                if (mOnBackClickListener!=null){
                    mOnBackClickListener.onClick(v);
                }
            }
        }
    };

    public void setTitle(String title){
        mGalleryNameText.setText(title);
    }

    public void enableRightButton(boolean enable){
        mAheadBtn.setEnabled(enable);
    }

    public void setRightButtonStatus(boolean show) {
        mAheadBtn.setVisibility(show ? VISIBLE : GONE);
    }

    public AlbumTitleBar setOnBackClickListener(OnClickListener onBackClickListener) {
        this.mOnBackClickListener = onBackClickListener;
        return this;
    }

    public AlbumTitleBar setOnAheadClickListener(OnClickListener onAheadClickListener) {
        this.mOnAheadClickListener = onAheadClickListener;
        return this;
    }

    public AlbumTitleBar setOnGalleryClickListener(OnClickListener onGalleryClickListener) {
        this.mOnGalleryClickListener = onGalleryClickListener;
        return this;
    }
}
