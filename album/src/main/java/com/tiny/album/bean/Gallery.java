package com.tiny.album.bean;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * 功能描述：
 * Created by pumengxia on 2018/4/27 0027 下午 5:42.
 */

public class Gallery {
    public int galleryId;
    public String galleryName;
    public TreeMap<Integer,Photo> mPhotos = new TreeMap<>(new Comparator<Integer>(){

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2-o1;
        }
    });
}
