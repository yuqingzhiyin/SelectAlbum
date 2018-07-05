package com.tiny.album;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tiny.album.bean.Gallery;
import com.tiny.album.bean.Photo;
import com.tiny.album.imageloader.ImageLoaderFactory;
import com.tiny.album.imageloader.ImageLoaderWrapper;
import com.tiny.album.presenter.AlbumPresenterImpl;
import com.tiny.album.presenter.IAlbumPresenter;
import com.tiny.album.presenter.entity.ImageScanResult;
import com.tiny.album.ui.activity.PreviewActivity;
import com.tiny.album.ui.adapter.GalleryAdapter;
import com.tiny.album.ui.adapter.GalleryNameAdapter;
import com.tiny.album.ui.divider.GridSpacingItemDecoration;
import com.tiny.album.ui.view.AlbumTitleBar;
import com.tiny.album.view.IAlbumView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tiny.album.ui.activity.PreviewActivity.NEED_FINISH;
import static com.tiny.album.ui.activity.PreviewActivity.NEED_REFRESH;

public class AlbumActivity extends AppCompatActivity implements IAlbumView,GalleryAdapter.IGalleryListener {
    public static final String KEY_NEED_CAMERA = "need_camera";
    public static final String KEY_MAX_SELECT_NUM = "multi_max_select_num";
    private static final String PACKAGE_URL_SCHEME = "package:";
    public static final String REQUEST_DATA = "data";
    private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    private static final int REQUEST_CAMERA_CODE = 2;
    private static final int REQUEST_CODE_FROM_CAMERA = 3;
    private static final int REQUEST_CODE_PREVIEW = 4;

    private static final int INIT_DISABLE_ID = 0;
    private static final int GALLERY_SPAN = 4;
    private static final int Span_SPACING = 12;

    private RecyclerView mGalleryRecycler;
    private AlbumTitleBar mTitleBar;
    private GalleryAdapter mGalleryAdapter;
    private PopupWindow mPopupWindow;
    private Rect mPopRect = new Rect();
    private String mCurrentPath;

    private int mGalleryId = INIT_DISABLE_ID;
    private int mMaxSelectNum;
    private boolean mIsOpenCamera;
    private IAlbumPresenter mPresenter;
    private ImageLoaderWrapper mImageLoaderWrapper;

    public static void launch(Activity activity, int requestCode, boolean needCamera, int maxSelectNum) {
        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putExtra(KEY_NEED_CAMERA, needCamera);
        intent.putExtra(KEY_MAX_SELECT_NUM, maxSelectNum);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsOpenCamera = getIntent().getBooleanExtra(KEY_NEED_CAMERA, true);
        mMaxSelectNum = getIntent().getIntExtra(KEY_MAX_SELECT_NUM, 1);
        mImageLoaderWrapper = ImageLoaderFactory.getLoader();
        setContentView(R.layout.activity_album);
        initView();
        initListener();
        mPresenter = new AlbumPresenterImpl();
        mPresenter.attach(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            mPresenter.startScanImage(getApplicationContext(), getSupportLoaderManager());
        }else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    REQUEST_READ_EXTERNAL_STORAGE_CODE,
                    R.string.grant_advice_read_album);
        }
    }
    private void initView(){
        mGalleryRecycler = findViewById(R.id.rv_gallery);
        mGalleryAdapter = new GalleryAdapter(this,mImageLoaderWrapper);
        mGalleryAdapter.setIsShowCamera(mIsOpenCamera);
        mGalleryAdapter.setGalleryListener(this);
        mGalleryRecycler.setAdapter(mGalleryAdapter);
        mGalleryRecycler.addItemDecoration(new GridSpacingItemDecoration(GALLERY_SPAN,Span_SPACING,false));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,GALLERY_SPAN);
        mGalleryRecycler.setLayoutManager(gridLayoutManager);
        mTitleBar = findViewById(R.id.tb_title);
        mTitleBar.enableRightButton(false);
        mTitleBar.setRightButtonStatus(mMaxSelectNum>1);
    }

    private void requestPermission(String permission,int requestCode,int warnRes) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, warnRes, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    private void initListener() {
        mTitleBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setOnGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGalleryPopupWindow();
            }
        });
        mTitleBar.setOnAheadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectComplete(ImageScanResult.getSelectPhotos());
            }
        });
    }

    private void selectComplete(ArrayList<String> selectPhotos) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(REQUEST_DATA, selectPhotos);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void showGalleryPopupWindow() {
        if (mPopupWindow == null){
            List<Gallery> galleryList = mPresenter.getGalleries();
            if (galleryList.size()<1){
                return;
            }
            initPpopuWindow(galleryList);
            mPopupWindow.showAsDropDown(mTitleBar, 0, 0);
        }else if (mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }else {
            mPopupWindow.showAsDropDown(mTitleBar, 0, 0);
        }

    }

    private void initPpopuWindow(List<Gallery> galleryList){
        RecyclerView recyclerView = (RecyclerView) View.inflate(this, R.layout.layout_album_pop, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        GalleryNameAdapter.IGalleryNameListener listener = new GalleryNameAdapter.IGalleryNameListener() {
            @Override
            public void changeAlbum(Gallery gallery) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                mGalleryId = gallery.galleryId;
                updateGallery(gallery);
            }
        };
        GalleryNameAdapter adapter = new GalleryNameAdapter(this, galleryList,listener);
        recyclerView.setAdapter(adapter);

        int width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.5f);
        //确定显示的高度
        int height = (int) (getResources().getDimension(R.dimen.pop_item_height) * galleryList.size());
        int limitHeight = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.6f);
        if (height > limitHeight) {
            height = limitHeight;
        }
        mPopupWindow = new PopupWindow(recyclerView, width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //popwindow显示时点击外部不会触发图片被点击，仅仅使window消失
        if (mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
            int x = (int) ev.getRawX();
            int y = (int) ev.getRawY();
            View view = mPopupWindow.getContentView();
            view.getGlobalVisibleRect(mPopRect);
            if (!mPopRect.contains(x,y)){
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void clickCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }else {
            requestPermission(Manifest.permission.CAMERA,REQUEST_CAMERA_CODE,R.string.grant_advice_open_camera);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
        ImageScanResult.reset();
    }

    @Override
    public boolean selectPhoto(Photo photo) {
        boolean select = photo.isSelect;
        ArrayList<String> selectedPhotos = ImageScanResult.getSelectPhotos();
        if (!select){
            int selectNum = selectedPhotos.size();
            if (selectNum >= mMaxSelectNum) {
                Toast.makeText(this,getString(R.string.select_max_num)+selectNum,Toast.LENGTH_SHORT).show();
                return false;
            }
            photo.isSelect = true;
            if (!selectedPhotos.contains(photo.path)) {
                selectedPhotos.add(photo.path);
            }
        }else {
            photo.isSelect = false;
            selectedPhotos.remove(photo.path);
        }
        if (mMaxSelectNum>1){
            mTitleBar.enableRightButton(selectedPhotos.size()>0);
        }else {
            selectComplete(selectedPhotos);
        }
        return true;
    }

    @Override
    public void goPreview(Photo photo) {
        PreviewActivity.launch(this,REQUEST_CODE_PREVIEW,mGalleryId,photo,mMaxSelectNum);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.startScanImage(getApplicationContext(), getSupportLoaderManager());
            } else {
                showMissingPermissionDialog(getString(R.string.help_album_content),getString(R.string.grant_album_permission_failure));
            }
        }else if (requestCode == REQUEST_CAMERA_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }else {
                showMissingPermissionDialog(getString(R.string.help_camera_content),
                        getString(R.string.grant_camera_permission_failure));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_CAMERA:
                    Intent intent = new Intent();
                    ArrayList<String> list = new ArrayList<>();
                    list.add(mCurrentPath);
                    intent.putStringArrayListExtra(REQUEST_DATA, list);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case REQUEST_CODE_PREVIEW:
                    boolean refresh = data.getBooleanExtra(NEED_REFRESH,true);
                    if (refresh){
                        mGalleryAdapter.notifyDataSetChanged();
                    }
                    boolean needFinish = data.getBooleanExtra(NEED_FINISH,false);
                    if (needFinish){
                        Intent finishIntent = new Intent();
                        finishIntent.putStringArrayListExtra(REQUEST_DATA, ImageScanResult.getSelectPhotos());
                        setResult(Activity.RESULT_OK, finishIntent);
                        finish();
                    }
                    break;
            }
        }
    }

    private void openCamera(){
        String parentPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/gallery";
        File file = new File(parentPath, "/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        mCurrentPath = file.getAbsolutePath();
        ContentValues newValues = new ContentValues(2);
        newValues.put(MediaStore.Images.Media.DATA, mCurrentPath);
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        Uri uri =getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
    }
    /**
     * 显示打开权限提示的对话框
     */
    private void showMissingPermissionDialog(String prompt, final String failure) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.system_permission);
        builder.setMessage(prompt);

        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AlbumActivity.this, failure, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemSettings();
                finish();
            }
        });

        builder.show();
    }

    /**
     * 启动系统权限设置界面
     */
    private void startSystemSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void refreshAlbumData(List<Gallery> galleries) {
        if (galleries.size() == 0){
            return;
        }
        //判断以防新插入图片导致相册内容变化
        if (mGalleryId == INIT_DISABLE_ID) {
            Gallery gallery = galleries.get(0);
            mGalleryId = gallery.galleryId;
            updateGallery(gallery);
        }
    }

    public void updateGallery(Gallery gallery){
        mTitleBar.setTitle(gallery.galleryName);
        mGalleryAdapter.setData(gallery.mPhotos);
        mGalleryAdapter.notifyDataSetChanged();
    }
}
