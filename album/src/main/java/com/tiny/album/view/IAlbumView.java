package com.tiny.album.view;

import com.tiny.album.bean.Gallery;

import java.util.List;

/**
 * 功能描述：
 * Created by pumengxia on 2018/5/24 0024 下午 5:51.
 */

public interface IAlbumView {
    void refreshAlbumData(List<Gallery> galleries);
}
