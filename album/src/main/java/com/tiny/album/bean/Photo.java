package com.tiny.album.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * 功能描述：图片bean
 * Created by pumengxia on 2018/5/28 0028 上午 10:38.
 */

public class Photo implements Serializable {
    public String path;
    public boolean isSelect = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return isSelect == photo.isSelect &&
                path.equals(photo.path);
    }
}
