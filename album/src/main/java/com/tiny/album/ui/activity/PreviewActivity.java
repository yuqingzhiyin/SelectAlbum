package com.tiny.album.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.tiny.album.R;
import com.tiny.album.bean.Photo;
import com.tiny.album.imageloader.ImageLoaderFactory;
import com.tiny.album.imageloader.ImageLoaderWrapper;
import com.tiny.album.presenter.IPreviewPresenter;
import com.tiny.album.presenter.PreviewPresenterImpl;
import com.tiny.album.presenter.entity.ImageScanResult;
import com.tiny.album.ui.adapter.PreviewPagerAdapter;
import com.tiny.album.ui.view.AlbumTitleBar;
import com.tiny.album.view.IPreviewView;

import java.util.List;

public class PreviewActivity extends AppCompatActivity implements IPreviewView, PreviewPagerAdapter.IPagerListener {
    private static final String ALBUM_ID = "album_id";
    private static final String PHOTO_NOW = "photo_now";
    private static final String MAX_NUM = "max_num";
    public static final String NEED_REFRESH = "need_refresh";
    public static final String NEED_FINISH = "need_finish";
    private AlbumTitleBar mHeaderView;
    private View mFooterView;
    private ViewPager mPreviewViewPager;
    private TextView mSelectView;
    private ImageLoaderWrapper mImageLoaderWrapper;
    private PreviewPagerAdapter mPreviewPagerAdapter;
    private IPreviewPresenter mPreviewPresenter;
    private int mMaxNum;
    private boolean mNeedRefresh; //标志返回后需要刷新

    public static void launch(Activity context, int requestCode,int albumId, Photo photo,int maxNum){
        Intent intent = new Intent(context,PreviewActivity.class);
        intent.putExtra(ALBUM_ID,albumId);
        intent.putExtra(PHOTO_NOW,photo);
        intent.putExtra(MAX_NUM,maxNum);
        context.startActivityForResult(intent,requestCode);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setPositionToTitle(position);
            Photo photo = mPreviewPresenter.getPhotos().get(position);
            mSelectView.setSelected(photo.isSelect);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(NEED_REFRESH, mNeedRefresh);
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mImageLoaderWrapper = ImageLoaderFactory.getLoader();

        initView();
        if (Build.VERSION.SDK_INT >= 11) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (View.SYSTEM_UI_FLAG_VISIBLE == visibility) {//此处需要添加顶部和底部消失和出现的动画效果
                        mHeaderView.startAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.top_enter_anim));
                        mFooterView.startAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.bottom_enter_anim));

                    } else {
                        mHeaderView.startAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.top_exit_anim));
                        mFooterView.startAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.bottom_exit_anim));

                    }
                }
            });
        }
        Photo photo = (Photo) getIntent().getSerializableExtra(PHOTO_NOW);
        int nowAlbum = getIntent().getIntExtra(ALBUM_ID,-1);
        mMaxNum = getIntent().getIntExtra(MAX_NUM,1);
        mPreviewPresenter = new PreviewPresenterImpl();
        mPreviewPresenter.attach(this);
        mPreviewPresenter.getNowPhotos(nowAlbum,photo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreviewPresenter.detach();
        mPreviewViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    private void initView() {
        mHeaderView = findViewById(R.id.header_view);
        mFooterView = findViewById(R.id.footer_view);
        mPreviewViewPager = findViewById(R.id.vp_preview);
        mSelectView = findViewById(R.id.tv_image_select);
        mPreviewPagerAdapter = new PreviewPagerAdapter(this,mImageLoaderWrapper,this);
        mPreviewViewPager.setAdapter(mPreviewPagerAdapter);
        enableRightButton();
        mHeaderView.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mHeaderView.setOnAheadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(NEED_REFRESH, false);
                data.putExtra(NEED_FINISH,true);
                setResult(Activity.RESULT_OK, data);
            }
        });
        mSelectView.setOnClickListener(mOnClickListener);
        mPreviewViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void selectPos(Integer position) {
        setPositionToTitle(position);
        List<Photo> photoList = mPreviewPresenter.getPhotos();
        boolean select = photoList.get(position).isSelect;
        mSelectView.setSelected(select);
        mPreviewPagerAdapter.setData(photoList);
        mPreviewPagerAdapter.notifyDataSetChanged();
        mPreviewViewPager.setCurrentItem(position);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_image_select){
                int currentPosition = mPreviewViewPager.getCurrentItem();
                Photo photo = mPreviewPresenter.getPhotos().get(currentPosition);
                boolean select = !photo.isSelect;
                List<String> selectPhotos = ImageScanResult.getSelectPhotos();
                if (select && selectPhotos.size() >= mMaxNum) {
                    Toast.makeText(PreviewActivity.this,getString(R.string.select_max_num)+selectPhotos.size(),Toast.LENGTH_SHORT).show();
                    return;
                }
                enableRightButton();
                mNeedRefresh = true;
                photo.isSelect = select;
                mSelectView.setSelected(photo.isSelect);
            }
        }
    };

    private void enableRightButton(){
        if (ImageScanResult.getSelectPhotos().size()>0){
            mHeaderView.enableRightButton(true);
        }else {
            mHeaderView.enableRightButton(false);
        }
    }

    /**
     * 设置标题现实当前所处的位置
     *
     * @param position
     */
    private void setPositionToTitle(int position) {
        int size = mPreviewPresenter.getPhotos().size();
        String title = String.format(getString(R.string.image_index), position + 1, size);
        mHeaderView.setTitle(title);
    }

    @Override
    public void toggleImmersiveMode() {
        if (Build.VERSION.SDK_INT >= 11) {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }
}
