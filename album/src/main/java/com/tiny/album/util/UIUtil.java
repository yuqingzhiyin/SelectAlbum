package com.tiny.album.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;

/**
 * 功能描述：UI工具类
 * Created by pumengxia on 2018/5/3 0003 下午 4:41.
 */

public class UIUtil {
    private static float mDensity;
    private static UIUtil mUiUtil;
    private static int mSoftButtonsBarHeight = 0;
    private static int mScreenWidth = 0,mScreenHeight = 0;

    private UIUtil() {
    }

    public static UIUtil getInstance(){
        if (mUiUtil == null){
            mUiUtil = new UIUtil();
        }
        return mUiUtil;
    }

    public void init(Activity activity) {
        DisplayMetrics dm = activity.getApplicationContext().getResources().getDisplayMetrics();
        mDensity = dm.density;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        calculateSoftButtonsBarHeight(activity);
    }

    public int getScreenWidth(){
        return mScreenWidth;
    }

    public int getScreenHeight(){
        return mScreenHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    public void calculateSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            Class c;
            try {
                c = Class.forName("android.view.Display");
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(activity.getWindowManager().getDefaultDisplay(), metrics);
            } catch (Exception e) {
                metrics.setToDefaults();
                e.printStackTrace();
            }
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            mSoftButtonsBarHeight=realHeight - usableHeight;
        }
    }

    /**
     * 初始化后直接获取虚拟按键高度
     * @return
     */
    public int getSoftButtonsBarHeight(){
        return mSoftButtonsBarHeight;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    public int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int supportSoftInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // 某些Android版本下，减去底部虚拟按键栏的高度（如果有的话）
            supportSoftInputHeight = supportSoftInputHeight - mSoftButtonsBarHeight;
        }
        return supportSoftInputHeight;
    }


    public static int dp2px(float dipValue) {
        return (int) (dipValue * mDensity + 0.5f);
    }

    public static float dp2px(Context context,float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return value * scale / 160 + 0.5f;
    }
}
